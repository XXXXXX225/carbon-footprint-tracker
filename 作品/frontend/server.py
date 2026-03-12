#!/usr/bin/env python3
"""
Simple HTTP server for serving static files and handling API proxy
"""
import http.server
import socketserver
import urllib.request
import urllib.error
import json

PORT = 8080
API_BASE_URL = "http://localhost:8888"

class CORSRequestHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type, Authorization')
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()

    def do_GET(self):
        # Proxy GET requests to backend
        if self.path.startswith('/api/'):
            self._proxy_request('GET')
        else:
            super().do_GET()

    def do_POST(self):
        # Proxy POST requests to backend
        if self.path.startswith('/api/'):
            self._proxy_request('POST')
        else:
            super().do_GET()

    def do_PUT(self):
        # Proxy PUT requests to backend
        if self.path.startswith('/api/'):
            self._proxy_request('PUT')
        else:
            self.send_error(405, "Method Not Allowed")

    def do_DELETE(self):
        # Proxy DELETE requests to backend
        if self.path.startswith('/api/'):
            self._proxy_request('DELETE')
        else:
            self.send_error(405, "Method Not Allowed")

    def _proxy_request(self, method):
        try:
            # Read request body
            content_length = int(self.headers.get('Content-Length', 0))
            body = self.rfile.read(content_length) if content_length > 0 else None

            # Build target URL
            target_url = API_BASE_URL + self.path

            # Create request
            req = urllib.request.Request(
                target_url,
                data=body,
                method=method,
                headers={
                    'Content-Type': self.headers.get('Content-Type', 'application/json'),
                    'Authorization': self.headers.get('Authorization', '')
                }
            )

            # Send request to backend
            with urllib.request.urlopen(req) as response:
                self.send_response(response.status)
                for header, value in response.headers.items():
                    if header.lower() not in ['transfer-encoding', 'content-length']:
                        self.send_header(header, value)
                self.end_headers()
                self.wfile.write(response.read())

        except urllib.error.HTTPError as e:
            self.send_response(e.code)
            self.end_headers()
            self.wfile.write(e.read())
        except Exception as e:
            self.send_response(500)
            self.end_headers()
            self.wfile.write(json.dumps({'error': str(e)}).encode())

if __name__ == '__main__':
    import os
    os.chdir(os.path.dirname(os.path.abspath(__file__)))

    with socketserver.TCPServer(("", PORT), CORSRequestHandler) as httpd:
        print(f"Serving at http://localhost:{PORT}/")
        print(f"API proxy to {API_BASE_URL}")
        httpd.serve_forever()
