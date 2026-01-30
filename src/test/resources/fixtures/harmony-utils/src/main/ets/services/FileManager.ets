import fs from '@ohos.file.fs';
import { Logger } from '../utils/Logger';
import { BusinessError } from '@ohos.base';

export interface FileInfo {
  uri: string;
  name: string;
  size: number;
  lastModified: number;
  isDirectory: boolean;
  mimeType?: string;
}

export class FileManager {
  private static readonly TAG = 'FileManager';

  // 获取文件列表
  static async listFiles(directoryUri: string): Promise<FileInfo[]> {
    try {
      const files: FileInfo[] = [];
      const listResult = await fs.listFile(directoryUri);
      const entries = listResult;

      for (let i = 0; i < entries.length; i++) {
        const entry = entries[i];
        const fullPath = `${directoryUri}/${entry}`;
        const stat = fs.statSync(fullPath);
        files.push({
          uri: fullPath,
          name: entry,
          size: stat.size,
          lastModified: stat.mtime,
          isDirectory: stat.isDirectory()
        });
      }

      Logger.success(`获取文件列表成功，共 ${files.length} 个文件`);
      return files;
    } catch (error) {
      Logger.error('获取文件列表失败', error as BusinessError);
      return [];
    }
  }

  // 复制文件
  static async copyFile(sourceUri: string, targetUri: string): Promise<boolean> {
    try {
      fs.copyFileSync(sourceUri, targetUri);
      Logger.success(`文件复制成功: ${sourceUri} -> ${targetUri}`);
      return true;
    } catch (error) {
      Logger.error('文件复制失败', error as BusinessError);
      return false;
    }
  }

  // 移动文件
  static async moveFile(sourceUri: string, targetUri: string): Promise<boolean> {
    try {
      fs.renameSync(sourceUri, targetUri);
      Logger.success(`文件移动成功: ${sourceUri} -> ${targetUri}`);
      return true;
    } catch (error) {
      Logger.error('文件移动失败', error as BusinessError);
      return false;
    }
  }

  // 删除文件
  static async deleteFile(fileUri: string): Promise<boolean> {
    try {
      fs.unlinkSync(fileUri);
      Logger.success(`文件删除成功: ${fileUri}`);
      return true;
    } catch (error) {
      Logger.error('文件删除失败', error as BusinessError);
      return false;
    }
  }

  // 删除目录
  static async deleteDirectory(directoryUri: string, recursive: boolean = false): Promise<boolean> {
    try {
      // 先检查是否递归删除
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
      // 使用 fs.rmdir 删除空目录
      fs.rmdirSync(directoryUri);
      Logger.success(`目录删除成功: ${directoryUri}`);
      return true;
    } catch (error) {
      Logger.error('目录删除失败', error as BusinessError);
      return false;
    }
  }

  // 创建目录
  static async createDirectory(directoryUri: string): Promise<boolean> {
    try {
      fs.mkdirSync(directoryUri);
      Logger.success(`目录创建成功: ${directoryUri}`);
      return true;
    } catch (error) {
      Logger.error('目录创建失败', error as BusinessError);
      return false;
    }
  }

  // 获取文件信息
  static async getFileInfo(fileUri: string): Promise<FileInfo> {
    try {
      const stat = fs.statSync(fileUri);
      const fileName = fileUri.substring(fileUri.lastIndexOf('/') + 1);
      const result: FileInfo = {
        uri: fileUri,
        name: fileName,
        size: stat.size,
        lastModified: stat.mtime,
        isDirectory: stat.isDirectory()
      };
      Logger.success('获取文件信息成功');
      return result;
    } catch (error) {
      Logger.error('获取文件信息失败', error as BusinessError);
      return { uri: '', name: '', size: 0, lastModified: 0, isDirectory: false };
    }
  }

  // 搜索文件
  static async searchFiles(directoryUri: string, pattern: string): Promise<FileInfo[]> {
    try {
      const results: FileInfo[] = [];
      const listResult = await fs.listFile(directoryUri);
      const entries = listResult;

      const regex = new RegExp(pattern, 'i');

      for (let i = 0; i < entries.length; i++) {
        const entry = entries[i];
        if (regex.test(entry)) {
          const fullPath = `${directoryUri}/${entry}`;
          const stat = fs.statSync(fullPath);
          results.push({
            uri: fullPath,
            name: entry,
            size: stat.size,
            lastModified: stat.mtime,
            isDirectory: stat.isDirectory()
          });
        }
      }

      Logger.success(`搜索完成，找到 ${results.length} 个匹配文件`);
      return results;
    } catch (error) {
      Logger.error('文件搜索失败', error as BusinessError);
      return [];
    }
  }

  // 读取文本文件
  static async readTextFile(fileUri: string): Promise<string> {
    try {
      const stat = fs.statSync(fileUri);
      const file = fs.openSync(fileUri, 0o1); // READ_ONLY = 0o1
      const arrayBuffer = new ArrayBuffer(stat.size);
      fs.readSync(file.fd, arrayBuffer);
      fs.closeSync(file);
      const uint8Array = new Uint8Array(arrayBuffer);
      let text = '';
      for (let i = 0; i < uint8Array.length; i++) {
        text += String.fromCharCode(uint8Array[i]);
      }
      Logger.success(`文本文件读取成功，长度: ${stat.size}`);
      return text;
    } catch (error) {
      Logger.error('文本文件读取失败', error as BusinessError);
      return '';
    }
  }

  // 写入文本文件
  static async writeTextFile(fileUri: string, content: string, append: boolean = false): Promise<boolean> {
    try {
      const mode = append ? (0o2 | 0o1000) : (0o2 | 0o100); // WRITE_ONLY | APPEND or WRITE_ONLY | CREATE
      const file = fs.openSync(fileUri, mode);
      const arrayBuffer = new ArrayBuffer(content.length);
      const uint8Array = new Uint8Array(arrayBuffer);
      for (let i = 0; i < content.length; i++) {
        uint8Array[i] = content.charCodeAt(i);
      }
      fs.writeSync(file.fd, arrayBuffer);
      fs.closeSync(file);
      Logger.success(`文本文件写入成功，长度: ${content.length}`);
      return true;
    } catch (error) {
      Logger.error('文本文件写入失败', error as BusinessError);
      return false;
    }
  }

  // 格式化文件大小
  static formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
  }

  // 格式化时间戳
  static formatTimestamp(timestamp: number): string {
    const date = new Date(timestamp);
    return date.toLocaleString('zh-CN');
  }
}
