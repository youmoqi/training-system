# 前端样式指南

## 概述

本项目采用统一的设计系统，确保整个应用的一致性和可维护性。所有样式都基于CSS变量系统，便于主题定制和响应式设计。

## 设计系统

### 颜色系统

```css
/* 主色调 */
--primary-color: #409EFF;      /* 主色 */
--primary-light: #79BBFF;      /* 主色浅色 */
--primary-dark: #337ECC;       /* 主色深色 */

/* 功能色 */
--success-color: #67C23A;      /* 成功色 */
--warning-color: #E6A23C;      /* 警告色 */
--danger-color: #F56C6C;       /* 危险色 */
--info-color: #909399;         /* 信息色 */

/* 文字颜色 */
--text-primary: #303133;       /* 主要文字 */
--text-regular: #606266;       /* 常规文字 */
--text-secondary: #909399;     /* 次要文字 */
--text-placeholder: #C0C4CC;   /* 占位文字 */

/* 边框颜色 */
--border-color: #DCDFE6;       /* 主要边框 */
--border-light: #E4E7ED;       /* 浅色边框 */
--border-lighter: #EBEEF5;     /* 更浅边框 */
--border-extra-light: #F2F6FC; /* 最浅边框 */

/* 背景颜色 */
--bg-color: #F5F7FA;          /* 页面背景 */
--bg-white: #FFFFFF;           /* 白色背景 */
--bg-page: #F5F7FA;           /* 页面背景 */
```

### 间距系统

```css
--spacing-xs: 4px;    /* 超小间距 */
--spacing-sm: 8px;    /* 小间距 */
--spacing-md: 16px;   /* 中等间距 */
--spacing-lg: 24px;   /* 大间距 */
--spacing-xl: 32px;   /* 超大间距 */
--spacing-xxl: 48px;  /* 最大间距 */
```

### 字体系统

```css
--font-size-xs: 12px;    /* 超小字体 */
--font-size-sm: 14px;    /* 小字体 */
--font-size-base: 16px;  /* 基础字体 */
--font-size-lg: 18px;    /* 大字体 */
--font-size-xl: 20px;    /* 超大字体 */
--font-size-xxl: 24px;   /* 最大字体 */
```

### 圆角系统

```css
--border-radius-small: 4px;      /* 小圆角 */
--border-radius-base: 6px;       /* 基础圆角 */
--border-radius-large: 8px;      /* 大圆角 */
--border-radius-extra-large: 12px; /* 超大圆角 */
```

### 阴影系统

```css
--shadow-light: 0 2px 12px 0 rgba(0, 0, 0, 0.1);     /* 浅阴影 */
--shadow-base: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04); /* 基础阴影 */
--shadow-dark: 0 4px 16px rgba(0, 0, 0, 0.15);       /* 深阴影 */
```

## 布局组件

### 页面容器

```html
<div class="page-container">
  <!-- 页面内容 -->
</div>
```

### 页面头部

```html
<div class="page-header">
  <h3 class="page-title">页面标题</h3>
  <div class="page-actions">
    <!-- 操作按钮 -->
  </div>
</div>
```

### 卡片组件

```html
<div class="card">
  <div class="card-header">
    <h3 class="card-title">卡片标题</h3>
  </div>
  <div class="card-body">
    <!-- 卡片内容 -->
  </div>
  <div class="card-footer">
    <!-- 卡片底部 -->
  </div>
</div>
```

### 搜索栏

```html
<div class="search-section">
  <el-input
    v-model="searchKeyword"
    placeholder="搜索..."
    class="search-input"
    clearable
  />
</div>
```

### 表格容器

```html
<div class="table-container">
  <el-table :data="tableData">
    <!-- 表格内容 -->
  </el-table>
</div>
```

### 分页容器

```html
<div class="pagination-container">
  <el-pagination
    v-model:current-page="currentPage"
    v-model:page-size="pageSize"
    :total="total"
    layout="total, sizes, prev, pager, next, jumper"
  />
</div>
```

### 对话框

```html
<el-dialog v-model="dialogVisible" title="对话框标题" class="dialog-container">
  <div class="dialog-body">
    <!-- 对话框内容 -->
  </div>
  <template #footer>
    <div class="dialog-footer">
      <!-- 对话框底部按钮 -->
    </div>
  </template>
</el-dialog>
```

## 工具类

### 文本对齐

```css
.text-center { text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }
```

### 间距工具类

```css
.mb-0 { margin-bottom: 0; }
.mb-sm { margin-bottom: var(--spacing-sm); }
.mb-md { margin-bottom: var(--spacing-md); }
.mb-lg { margin-bottom: var(--spacing-lg); }
.mb-xl { margin-bottom: var(--spacing-xl); }

.mt-0 { margin-top: 0; }
.mt-sm { margin-top: var(--spacing-sm); }
.mt-md { margin-top: var(--spacing-md); }
.mt-lg { margin-top: var(--spacing-lg); }
.mt-xl { margin-top: var(--spacing-xl); }

.p-0 { padding: 0; }
.p-sm { padding: var(--spacing-sm); }
.p-md { padding: var(--spacing-md); }
.p-lg { padding: var(--spacing-lg); }
.p-xl { padding: var(--spacing-xl); }
```

### 弹性布局

```css
.flex { display: flex; }
.flex-center { display: flex; justify-content: center; align-items: center; }
.flex-between { display: flex; justify-content: space-between; align-items: center; }
.flex-column { display: flex; flex-direction: column; }

.gap-sm { gap: var(--spacing-sm); }
.gap-md { gap: var(--spacing-md); }
.gap-lg { gap: var(--spacing-lg); }
.gap-xl { gap: var(--spacing-xl); }
```

## 响应式设计

### 断点

```css
/* 移动端 */
@media (max-width: 768px) {
  .page-container {
    padding: var(--spacing-md);
  }
  
  .page-header {
    flex-direction: column;
    gap: var(--spacing-sm);
  }
  
  .search-section {
    flex-direction: column;
  }
  
  .search-input {
    width: 100%;
  }
}
```

## 最佳实践

### 1. 使用CSS变量

```css
/* ✅ 正确 */
.my-component {
  color: var(--text-primary);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-base);
}

/* ❌ 错误 */
.my-component {
  color: #303133;
  padding: 24px;
  border-radius: 6px;
}
```

### 2. 组件样式结构

```vue
<style scoped>
/* 页面特定样式 */
.my-page {
  /* 使用全局样式类，无需额外样式 */
}

/* 自定义样式覆盖 */
:deep(.el-table) {
  border-radius: var(--border-radius-base);
}

:deep(.el-table th) {
  background-color: var(--bg-extra-light);
  color: var(--text-primary);
  font-weight: 600;
}

:deep(.el-table td) {
  color: var(--text-regular);
}
</style>
```

### 3. 按钮样式

```css
/* 主要按钮 */
.btn-primary {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: white;
}

.btn-primary:hover {
  background: var(--primary-dark);
  border-color: var(--primary-dark);
}
```

### 4. 标签样式

```css
.tag-primary {
  background: var(--primary-color);
  color: white;
}

.tag-success {
  background: var(--success-color);
  color: white;
}
```

## 动画效果

### 淡入淡出

```css
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
```

### 滑动效果

```css
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease;
}

.slide-enter-from {
  transform: translateX(-100%);
}

.slide-leave-to {
  transform: translateX(100%);
}
```

## 主题定制

要修改主题颜色，只需更新CSS变量：

```css
:root {
  --primary-color: #your-color;
  --primary-dark: #your-dark-color;
  --primary-light: #your-light-color;
}
```

## 注意事项

1. **保持一致性**：所有组件都应使用统一的设计系统
2. **响应式优先**：确保在所有设备上都有良好的体验
3. **可访问性**：确保颜色对比度符合WCAG标准
4. **性能优化**：避免过度使用阴影和动画效果
5. **维护性**：使用语义化的类名和CSS变量

## 文件结构

```
frontend/
├── src/
│   ├── styles/
│   │   └── common.css          # 全局样式
│   └── views/
│       ├── admin/              # 管理员页面
│       └── student/            # 学生页面
└── STYLE_GUIDE.md             # 样式指南
```

通过遵循这个样式指南，可以确保整个应用的一致性和可维护性。 