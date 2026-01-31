#!/usr/bin/env python3
"""
圈复杂度和Clean Code分析工具
分析Java代码库的复杂度和代码质量
"""

import os
import re
import json
from pathlib import Path
from typing import Dict, List, Tuple
from dataclasses import dataclass, field
from collections import defaultdict

@dataclass
class MethodInfo:
    """方法信息"""
    name: str
    start_line: int
    end_line: int
    complexity: int = 1
    nesting_level: int = 0
    parameters: int = 0
    is_getter: bool = False
    is_setter: bool = False

@dataclass
class ClassInfo:
    """类信息"""
    name: str
    file_path: str
    start_line: int
    end_line: int
    methods: List[MethodInfo] = field(default_factory=list)
    fields: int = 0
    inner_classes: int = 0

@dataclass
class FileAnalysis:
    """文件分析结果"""
    path: str
    lines: int = 0
    code_lines: int = 0
    comment_lines: int = 0
    blank_lines: int = 0
    classes: List[ClassInfo] = field(default_factory=list)

class JavaComplexityAnalyzer:
    """Java代码复杂度分析器"""

    def __init__(self, root_dir: str):
        self.root_dir = Path(root_dir)
        self.files: List[FileAnalysis] = []
        self.all_methods: List[Tuple[FileAnalysis, ClassInfo, MethodInfo]] = []

    def analyze(self) -> Dict:
        """执行完整分析"""
        print("开始分析Java代码...")
        print(f"根目录: {self.root_dir}")

        # 查找所有Java文件
        java_files = list(self.root_dir.rglob("*.java"))
        print(f"找到 {len(java_files)} 个Java文件")

        # 分析每个文件
        for java_file in java_files:
            if "test" not in str(java_file):  # 跳过测试文件
                analysis = self.analyze_file(java_file)
                self.files.append(analysis)

        # 收集所有方法
        for file_analysis in self.files:
            for class_info in file_analysis.classes:
                for method in class_info.methods:
                    self.all_methods.append((file_analysis, class_info, method))

        # 生成报告
        return self.generate_report()

    def analyze_file(self, file_path: Path) -> FileAnalysis:
        """分析单个Java文件"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
        except:
            try:
                with open(file_path, 'r', encoding='gbk') as f:
                    content = f.read()
            except:
                return FileAnalysis(path=str(file_path))

        lines = content.split('\n')
        analysis = FileAnalysis(path=str(file_path))
        analysis.lines = len(lines)

        # 统计代码行、注释行、空行
        for line in lines:
            stripped = line.strip()
            if not stripped:
                analysis.blank_lines += 1
            elif stripped.startswith('//') or stripped.startswith('/*') or stripped.startswith('*'):
                analysis.comment_lines += 1
            else:
                analysis.code_lines += 1

        # 解析类和方法
        analysis.classes = self.parse_classes_and_methods(content, file_path)

        return analysis

    def parse_classes_and_methods(self, content: str, file_path: Path) -> List[ClassInfo]:
        """解析类和方法"""
        classes = []
        lines = content.split('\n')

        # 移除注释和字符串内容，避免误判
        cleaned_content = self.remove_comments_and_strings(content)

        # 查找类定义
        class_pattern = r'(?:^|\n)\s*(?:public|private|protected)?\s*(?:abstract|final)?\s*(?:class|interface|enum)\s+(\w+)'
        class_matches = list(re.finditer(class_pattern, cleaned_content))

        for i, class_match in enumerate(class_matches):
            class_name = class_match.group(1)
            start_line = cleaned_content[:class_match.start()].count('\n') + 1

            # 查找类结束位置（下一个类开始或文件结束）
            if i + 1 < len(class_matches):
                end_line = cleaned_content[:class_matches[i + 1].start()].count('\n')
            else:
                end_line = len(lines)

            class_info = ClassInfo(
                name=class_name,
                file_path=str(file_path),
                start_line=start_line,
                end_line=end_line
            )

            # 解析类中的方法
            class_content = cleaned_content[class_match.start():]
            if i + 1 < len(class_matches):
                class_content = cleaned_content[class_match.start():class_matches[i + 1].start()]

            class_info.methods = self.parse_methods(class_content, start_line)
            class_info.fields = len(re.findall(r'\bprivate\s+\w+\s+\w+\s*;', class_content)) + \
                               len(re.findall(r'\bpublic\s+\w+\s+\w+\s*;', class_content)) + \
                               len(re.findall(r'\bprotected\s+\w+\s+\w+\s*;', class_content))
            class_info.inner_classes = len(re.findall(r'\bclass\s+\w+', class_content)) - 1

            classes.append(class_info)

        return classes

    def parse_methods(self, content: str, class_start_line: int) -> List[MethodInfo]:
        """解析方法并计算圈复杂度"""
        methods = []

        # 方法定义正则（包含构造函数）
        method_pattern = r'''
            (?:public|private|protected)?\s*
            (?:static|abstract|final|synchronized|native)?\s*
            (?:<[^>]+>)?\s*
            \w+\s+
            (\w+)\s*
            \([^)]*\)\s*
            (?:throws\s+[\w\s,]+)?\s*
            \{
        '''

        # 也匹配构造函数
        constructor_pattern = r'''
            (?:public|private|protected)?\s*
            (\w+)\s*
            \([^)]*\)\s*
            (?:throws\s+[\w\s,]+)?\s*
            \{
        '''

        lines = content.split('\n')
        method_matches = []

        # 使用更简单的方法匹配
        for i, line in enumerate(lines):
            stripped = line.strip()
            # 跳过注释、import、package等
            if (stripped.startswith('//') or stripped.startswith('/*') or
                stripped.startswith('*') or stripped.startswith('import') or
                stripped.startswith('package') or 'class ' in stripped or
                stripped.startswith('@')):
                continue

            # 检查是否是方法开始
            if '{' in stripped and '(' in stripped:
                # 简单判断：包含 { 且前面有 ( 且不是 if/for/while/switch
                before_brace = stripped.split('{')[0]
                if '(' in before_brace and not any(kw in before_brace for kw in ['if', 'for', 'while', 'switch', 'catch']):
                    # 可能是方法定义
                    method_match = self.extract_method_name_and_params(before_brace)
                    if method_match:
                        method_matches.append((i, method_match))

        # 为每个匹配的方法体计算复杂度
        for line_idx, (method_name, params) in method_matches:
            start_line = class_start_line + line_idx + 1

            # 找到方法结束
            brace_count = 0
            end_line = start_line
            for j in range(line_idx, len(lines)):
                brace_count += lines[j].count('{')
                brace_count -= lines[j].count('}')
                if brace_count == 0 and j > line_idx:
                    end_line = class_start_line + j + 1
                    break

            # 提取方法体
            method_body = '\n'.join(lines[line_idx:end_line - class_start_line])

            # 计算圈复杂度
            complexity = self.calculate_cyclomatic_complexity(method_body)
            nesting = self.calculate_nesting_level(method_body)

            method_info = MethodInfo(
                name=method_name,
                start_line=start_line,
                end_line=end_line,
                complexity=complexity,
                nesting_level=nesting,
                parameters=len(params),
                is_getter=method_name.startswith('get'),
                is_setter=method_name.startswith('set')
            )
            methods.append(method_info)

        return methods

    def extract_method_name_and_params(self, line: str) -> Tuple[str, List[str]]:
        """从方法签名中提取方法名和参数"""
        # 移除访问修饰符等
        line = re.sub(r'\b(public|private|protected|static|final|abstract|synchronized|native)\s+', '', line)
        line = re.sub(r'<[^>]+>', '', line)  # 移除泛型

        # 匹配 methodName(type param, ...)
        match = re.search(r'(\w+)\s*\(([^)]*)\)', line)
        if match:
            name = match.group(1)
            params_str = match.group(2)
            params = [p.strip() for p in params_str.split(',') if p.strip()]
            return (name, params)
        return None

    def remove_comments_and_strings(self, content: str) -> str:
        """移除注释和字符串字面量"""
        # 移除块注释
        content = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)
        # 移除行注释
        content = re.sub(r'//.*', '', content)
        # 移除字符串字面量
        content = re.sub(r'"([^"\\]|\\.)*"', '""', content)
        content = re.sub(r"'([^'\\]|\\.)*'", "''", content)
        return content

    def calculate_cyclomatic_complexity(self, method_body: str) -> int:
        """计算圈复杂度"""
        complexity = 1  # 基础复杂度

        # 移除注释和字符串
        body = self.remove_comments_and_strings(method_body)

        # 计算判断分支
        patterns = [
            (r'\bif\s*\(', 1),           # if语句
            (r'\belse\s+if\s*\(', 1),    # else if
            (r'\bfor\s*\(', 1),          # for循环
            (r'\bwhile\s*\(', 1),        # while循环
            (r'\bdo\s*{', 1),           # do-while
            (r'\bcase\s+', 1),          # case语句
            (r'\bdefault\s*:', 1),      # default语句
            (r'\bcatch\s*\(', 1),       # catch块
            (r'\?', 1),                 # 三元运算符
            (r'&&', 1),                 # 逻辑与（短路径）
            (r'\|\|', 1),               # 逻辑或（短路径）
        ]

        for pattern, value in patterns:
            matches = re.findall(pattern, body)
            complexity += len(matches) * value

        return complexity

    def calculate_nesting_level(self, method_body: str) -> int:
        """计算最大嵌套层级"""
        body = self.remove_comments_and_strings(method_body)
        max_level = 0
        current_level = 0

        for line in body.split('\n'):
            stripped = line.strip()
            # 检查块开始
            if any(kw in stripped for kw in ['if', 'for', 'while', 'try', 'catch', 'switch', 'else', 'do']):
                if '{' in stripped:
                    current_level += 1
                    max_level = max(max_level, current_level)
            # 简单的括号匹配
            open_braces = line.count('{')
            close_braces = line.count('}')
            current_level += open_braces - close_braces
            max_level = max(max_level, current_level)

        return max_level

    def generate_report(self) -> Dict:
        """生成分析报告"""
        # 统计数据
        total_methods = len(self.all_methods)
        total_classes = sum(len(f.classes) for f in self.files)
        total_files = len(self.files)
        total_lines = sum(f.lines for f in self.files)
        total_code_lines = sum(f.code_lines for f in self.files)

        # 方法长度分析
        long_methods = [
            (f, c, m) for f, c, m in self.all_methods
            if m.end_line - m.start_line > 50
        ]

        # 类大小分析
        large_classes = []
        for f in self.files:
            for c in f.classes:
                class_lines = c.end_line - c.start_line
                if class_lines > 300:
                    large_classes.append((f, c, class_lines))

        # 圈复杂度分析
        very_high_cc = [(f, c, m) for f, c, m in self.all_methods if m.complexity > 20]
        high_cc = [(f, c, m) for f, c, m in self.all_methods if 11 <= m.complexity <= 20]
        medium_cc = [(f, c, m) for f, c, m in self.all_methods if 6 <= m.complexity <= 10]
        good_cc = [(f, c, m) for f, c, m in self.all_methods if 1 <= m.complexity <= 5]

        # 嵌套层级分析
        deep_nesting = [(f, c, m) for f, c, m in self.all_methods if m.nesting_level > 4]

        # 按圈复杂度排序所有方法
        methods_by_cc = sorted(self.all_methods, key=lambda x: x[2].complexity, reverse=True)

        return {
            'summary': {
                'total_files': total_files,
                'total_lines': total_lines,
                'total_code_lines': total_code_lines,
                'total_comment_lines': sum(f.comment_lines for f in self.files),
                'total_blank_lines': sum(f.blank_lines for f in self.files),
                'total_classes': total_classes,
                'total_methods': total_methods,
                'avg_methods_per_class': total_methods / total_classes if total_classes > 0 else 0,
                'avg_lines_per_file': total_lines / total_files if total_files > 0 else 0,
            },
            'complexity_distribution': {
                'very_high_risk_cc_gt_20': len(very_high_cc),
                'high_risk_cc_11_20': len(high_cc),
                'medium_risk_cc_6_10': len(medium_cc),
                'good_cc_1_5': len(good_cc),
            },
            'code_quality': {
                'long_methods_gt_50_lines': len(long_methods),
                'large_classes_gt_300_lines': len(large_classes),
                'deep_nesting_gt_4_levels': len(deep_nesting),
            },
            'detailed_findings': {
                'very_high_cc_methods': very_high_cc,
                'high_cc_methods': high_cc,
                'medium_cc_methods': medium_cc,
                'long_methods': long_methods,
                'large_classes': large_classes,
                'deep_nesting_methods': deep_nesting,
            },
            'top_complex_methods': methods_by_cc[:30],
            'all_high_cc_methods': [(f, c, m) for f, c, m in methods_by_cc if m.complexity >= 6]
        }

def print_report(report: Dict):
    """打印格式化报告"""
    print("\n" + "="*80)
    print(" " * 25 + "Java代码复杂度分析报告")
    print("="*80)

    # 概览
    print("\n【1. 代码统计概览】")
    print("-" * 80)
    s = report['summary']
    print(f"  总文件数:        {s['total_files']:>6}")
    print(f"  总行数:          {s['total_lines']:>6}")
    print(f"  代码行数:        {s['total_code_lines']:>6}")
    print(f"  注释行数:        {s['total_comment_lines']:>6}")
    print(f"  空行数:          {s['total_blank_lines']:>6}")
    print(f"  总类数:          {s['total_classes']:>6}")
    print(f"  总方法数:        {s['total_methods']:>6}")
    print(f"  平均每类方法数:  {s['avg_methods_per_class']:.1f}")
    print(f"  平均每文件行数:  {s['avg_lines_per_file']:.1f}")

    # 圈复杂度分布
    print("\n【2. 圈复杂度分布】")
    print("-" * 80)
    cd = report['complexity_distribution']
    print(f"  极高风险 (CC > 20):    {cd['very_high_risk_cc_gt_20']:>3} 个方法")
    print(f"  高风险   (CC 11-20):   {cd['high_risk_cc_11_20']:>3} 个方法")
    print(f"  中等风险 (CC 6-10):    {cd['medium_risk_cc_6_10']:>3} 个方法")
    print(f"  优秀     (CC 1-5):     {cd['good_cc_1_5']:>3} 个方法")

    # 代码质量指标
    print("\n【3. Clean Code 评估】")
    print("-" * 80)
    cq = report['code_quality']
    print(f"  超长方法 (>50行):      {cq['long_methods_gt_50_lines']:>3} 个")
    print(f"  超大类   (>300行):     {cq['large_classes_gt_300_lines']:>3} 个")
    print(f"  深度嵌套 (>4层):       {cq['deep_nesting_gt_4_levels']:>3} 个")

    # Top 30 高复杂度方法
    print("\n【4. Top 30 高圈复杂度方法】")
    print("-" * 80)
    print(f"{'排名':<4} {'CC':<4} {'嵌套':<4} {'行数':<6} {'方法名':<30} {'文件':<40}")
    print("-" * 80)

    for i, (f, c, m) in enumerate(report['top_complex_methods'], 1):
        file_short = f.path.split('\\')[-1]
        method_len = m.end_line - m.start_line
        print(f"{i:<4} {m.complexity:<4} {m.nesting_level:<4} {method_len:<6} {m.name:<30} {file_short}")

    # 所有CC >= 6的方法
    print("\n【5. 所有需要关注的方法 (CC >= 6)】")
    print("-" * 80)
    print(f"{'CC':<4} {'嵌套':<4} {'行数':<6} {'位置':<60} {'方法名':<25}")
    print("-" * 80)

    for f, c, m in report['all_high_cc_methods']:
        file_short = f.path.split('\\')[-1]
        location = f"{file_short}:{m.name}:{m.start_line}"
        method_len = m.end_line - m.start_line
        print(f"{m.complexity:<4} {m.nesting_level:<4} {method_len:<6} {location:<60} {m.name}")

    # 超长方法
    print("\n【6. 超长方法列表 (>50行)】")
    print("-" * 80)
    print(f"{'行数':<6} {'位置':<70} {'方法名':<25}")
    print("-" * 80)

    for f, c, m in sorted(report['detailed_findings']['long_methods'],
                          key=lambda x: x[2].end_line - x[2].start_line, reverse=True):
        file_short = f.path.split('\\')[-1]
        location = f"{file_short}:{m.name}:{m.start_line}"
        method_len = m.end_line - m.start_line
        print(f"{method_len:<6} {location:<70} {m.name}")

    # 超大类
    print("\n【7. 超大类列表 (>300行)】")
    print("-" * 80)
    print(f"{'行数':<6} {'类名':<40} {'文件':<50}")
    print("-" * 80)

    for f, c, lines in sorted(report['detailed_findings']['large_classes'],
                              key=lambda x: x[2], reverse=True):
        file_short = f.path.split('\\')[-1]
        print(f"{lines:<6} {c.name:<40} {file_short}")

    print("\n" + "="*80)
    print("分析完成!")
    print("="*80 + "\n")

if __name__ == "__main__":
    root_dir = r"D:\code\ets-har-builder\ets2jsc\src\main\java"
    analyzer = JavaComplexityAnalyzer(root_dir)
    report = analyzer.analyze()
    print_report(report)

    # 保存JSON报告
    with open(r"D:\code\ets-har-builder\ets2jsc\complexity_report.json", 'w', encoding='utf-8') as f:
        json.dump(report, f, ensure_ascii=False, indent=2, default=str)
    print(f"\n详细报告已保存到: complexity_report.json")
