import fs from '@ohos.file.fs';
import { Logger } from '../utils/Logger';
import { BusinessError } from '@ohos.base';

export class FileManager {
  TAG = FileManager;

  static async listFiles(directoryUri /* string */) {
    try {
  const files = [];
  const listResult = await fs.listFile(directoryUri);
  const entries = listResult;
  for (let i = 0; i < entries.length; i++) {
  const entry = entries[i];
  const fullPath = {"kind":229,"kindName":"TemplateExpression"};
  const stat = fs.statSync(fullPath);
  files.push("{uri: fullPath, name: entry, size: stat.size, lastModified: stat.mtime, isDirectory: stat.isDirectory()}");
}
  Logger.success(`获取文件列表成功，共 ${files.length} 个文件`);
  return files;
} catch (error) {
  Logger.error("获取文件列表失败", error);
  return [];
}
  }

  static async copyFile(sourceUri /* string */, targetUri /* string */) {
    try {
  fs.copyFileSync(sourceUri, targetUri);
  Logger.success(`文件复制成功: ${sourceUri} -> ${targetUri}`);
  return true;
} catch (error) {
  Logger.error("文件复制失败", error);
  return false;
}
  }

  static async moveFile(sourceUri /* string */, targetUri /* string */) {
    try {
  fs.renameSync(sourceUri, targetUri);
  Logger.success(`文件移动成功: ${sourceUri} -> ${targetUri}`);
  return true;
} catch (error) {
  Logger.error("文件移动失败", error);
  return false;
}
  }

  static async deleteFile(fileUri /* string */) {
    try {
  fs.unlinkSync(fileUri);
  Logger.success(`文件删除成功: ${fileUri}`);
  return true;
} catch (error) {
  Logger.error("文件删除失败", error);
  return false;
}
  }

  static async deleteDirectory(directoryUri /* string */, recursive /* boolean */) {
    try {
  if (recursive) {
  const files = await FileManager.listFiles(directoryUri);
  for (const file of files) {
  if (file.isDirectory) {
  await FileManager.deleteDirectory(file.uri, true);
} else {
  await FileManager.deleteFile(file.uri);
}
}
}
  fs.rmdirSync(directoryUri);
  Logger.success(`目录删除成功: ${directoryUri}`);
  return true;
} catch (error) {
  Logger.error("目录删除失败", error);
  return false;
}
  }

  static async createDirectory(directoryUri /* string */) {
    try {
  fs.mkdirSync(directoryUri);
  Logger.success(`目录创建成功: ${directoryUri}`);
  return true;
} catch (error) {
  Logger.error("目录创建失败", error);
  return false;
}
  }

  static async getFileInfo(fileUri /* string */) {
    try {
  const stat = fs.statSync(fileUri);
  const fileName = fileUri.substring(fileUri.lastIndexOf('/') + 1);
  const result = {uri: , name: , size: , lastModified: , isDirectory: };
  Logger.success("获取文件信息成功");
  return result;
} catch (error) {
  Logger.error("获取文件信息失败", error);
  return {uri: , name: , size: , lastModified: , isDirectory: };
}
  }

  static async searchFiles(directoryUri /* string */, pattern /* string */) {
    try {
  const results = [];
  const listResult = await fs.listFile(directoryUri);
  const entries = listResult;
  const regex = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"RegExp","text":"RegExp"},"arguments":[{"kind":80,"kindName":"Identifier","name":"pattern","text":"pattern"},{"kind":11,"kindName":"StringLiteral","text":"i"}]};
  for (let i = 0; i < entries.length; i++) {
  const entry = entries[i];
  if (regex.test(entry)) {
  const fullPath = {"kind":229,"kindName":"TemplateExpression"};
  const stat = fs.statSync(fullPath);
  results.push("{uri: fullPath, name: entry, size: stat.size, lastModified: stat.mtime, isDirectory: stat.isDirectory()}");
}
}
  Logger.success(`搜索完成，找到 ${results.length} 个匹配文件`);
  return results;
} catch (error) {
  Logger.error("文件搜索失败", error);
  return [];
}
  }

  static async readTextFile(fileUri /* string */) {
    try {
  const stat = fs.statSync(fileUri);
  const file = fs.openSync(fileUri, 1);
  const arrayBuffer = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"ArrayBuffer","text":"ArrayBuffer"},"arguments":[{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"stat","text":"stat"},"name":"size","text":"stat.size"}]};
  fs.readSync(file.fd, arrayBuffer);
  fs.closeSync(file);
  const uint8Array = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Uint8Array","text":"Uint8Array"},"arguments":[{"kind":80,"kindName":"Identifier","name":"arrayBuffer","text":"arrayBuffer"}]};
  const text = "";
  for (let i = 0; i < uint8Array.length; i++) {
  text += String.fromCharCode(uint8Array[i]);
}
  Logger.success(`文本文件读取成功，长度: ${stat.size}`);
  return text;
} catch (error) {
  Logger.error("文本文件读取失败", error);
  return "";
}
  }

  static async writeTextFile(fileUri /* string */, content /* string */, append /* boolean */) {
    try {
  const mode = append ? (2 | 512) : (2 | 64);
  const file = fs.openSync(fileUri, mode);
  const arrayBuffer = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"ArrayBuffer","text":"ArrayBuffer"},"arguments":[{"kind":212,"kindName":"PropertyAccessExpression","expression":{"kind":80,"kindName":"Identifier","name":"content","text":"content"},"name":"length","text":"content.length"}]};
  const uint8Array = {"kind":215,"kindName":"NewExpression","expression":{"kind":80,"kindName":"Identifier","name":"Uint8Array","text":"Uint8Array"},"arguments":[{"kind":80,"kindName":"Identifier","name":"arrayBuffer","text":"arrayBuffer"}]};
  for (let i = 0; i < content.length; i++) {
  uint8Array[i] = content.charCodeAt(i);
}
  fs.writeSync(file.fd, arrayBuffer);
  fs.closeSync(file);
  Logger.success(`文本文件写入成功，长度: ${content.length}`);
  return true;
} catch (error) {
  Logger.error("文本文件写入失败", error);
  return false;
}
  }

  static formatFileSize(bytes /* number */) {
    if (bytes === 0) {
      return "0 B";
    }
    const k = 1024;
    const sizes = ["B", "KB", "MB", "GB", "TB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return (bytes / Math.pow(k, i)).toFixed(2) + " " + sizes[i];
  }

  static formatTimestamp(timestamp /* number */) {
    const date = new Date(timestamp);
    return date.toLocaleString("zh-CN");
  }

}


//# sourceMappingURL=FileManager.js.map