const fs = require('fs');

let content = fs.readFileSync('src/App.vue', 'utf8');
const targetFont = "'Noto Sans SC', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif";

// Element plus global variable mapping
if (content.includes('--el-font-family:')) {
    content = content.replace(/--el-font-family:\s*[^;]+;/, `--el-font-family: ${targetFont};`);
} else {
    content = content.replace('--primary-color: #4CAF50;', `--el-font-family: ${targetFont};\n  --primary-color: #4CAF50;`);
}

// Enforce body
content = content.replace(/(body\s*\{[\s\S]*?font-family:\s*)[^;]+;/, `$1${targetFont};`);

fs.writeFileSync('src/App.vue', content, 'utf8');
