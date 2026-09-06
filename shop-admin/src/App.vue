<template>
  <router-view />
</template>

<style>
:root {
  --c-bg: #fafafa;
  --c-surface: #ffffff;
  --c-border: #eaeaea;
  --c-border-hover: #d4d4d4;
  --c-text: #171717;
  --c-text-secondary: #6b7280;
  --c-text-muted: #9ca3af;
  --c-primary: #5c6cf0;
  --c-primary-hover: #4f5de0;
  --c-primary-light: #eef0ff;
  --c-success: #16a34a;
  --c-success-light: #dcfce7;
  --c-warning: #f59e0b;
  --c-warning-light: #fef3c7;
  --c-danger: #dc2626;
  --c-danger-light: #fee2e2;
  --c-info: #2563eb;
  --c-info-light: #dbeafe;
  --radius: 12px;
  --radius-sm: 8px;
  --radius-lg: 16px;
  --shadow-xs: 0 1px 2px rgba(0,0,0,0.03);
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.04), 0 1px 2px rgba(0,0,0,0.06);
  --shadow: 0 4px 6px rgba(0,0,0,0.03), 0 2px 4px rgba(0,0,0,0.04);
  --shadow-lg: 0 10px 25px rgba(0,0,0,0.05), 0 4px 10px rgba(0,0,0,0.04);
  --transition: 0.2s cubic-bezier(0.16,1,0.3,1);
}

* { margin:0; padding:0; box-sizing:border-box; }
html,body,#app { height:100%; font-family: -apple-system,BlinkMacSystemFont,'SF Pro Display','Segoe UI','PingFang SC','Microsoft YaHei',sans-serif; -webkit-font-smoothing:antialiased; color:var(--c-text); background:var(--c-bg); }

::-webkit-scrollbar { width:5px; }
::-webkit-scrollbar-thumb { background:#d4d4d4; border-radius:10px; }
::-webkit-scrollbar-track { background:transparent; }

.page { padding:32px 40px; min-height:calc(100vh - 56px); }

.toolbar {
  display:flex; align-items:center; justify-content:space-between; gap:12px;
  margin-bottom:24px; flex-wrap:wrap;
}
.toolbar-left { display:flex; align-items:center; gap:10px; flex-wrap:wrap; }

.el-card {
  border:1px solid var(--c-border) !important;
  border-radius:var(--radius-lg) !important;
  box-shadow:var(--shadow-xs) !important;
  transition:box-shadow var(--transition);
}
.el-card:hover { box-shadow:var(--shadow-sm) !important; }
.el-card__body { padding:24px !important; }

.el-table {
  --el-table-border-color:var(--c-border);
  --el-table-header-bg-color:#f9fafb;
  font-size:13px;
  border-radius:var(--radius-lg);
  overflow:hidden;
}
.el-table th.el-table__cell {
  background:#f9fafb;
  color:var(--c-text-secondary);
  font-weight:600;
  font-size:11px;
  text-transform:uppercase;
  letter-spacing:0.05em;
  border-bottom:1px solid var(--c-border);
  padding:12px 16px;
}
.el-table td.el-table__cell { padding:14px 16px; border-color:#f3f4f6; }
.el-table tr.el-table__row:hover>td.el-table__cell { background:#fafbfd; }

.el-pagination { margin-top:20px; justify-content:flex-end; display:flex; }
.el-pagination.is-background .el-pager li {
  border-radius:6px; border:1px solid var(--c-border); margin:0 2px;
}
.el-pagination.is-background .el-pager li.is-active {
  background:var(--c-primary); border-color:var(--c-primary);
}

.el-button { border-radius:var(--radius-sm); font-weight:500; letter-spacing:0.01em; transition:all var(--transition); }
.el-button--primary {
  --el-button-bg-color:var(--c-primary);
  --el-button-border-color:var(--c-primary);
  --el-button-hover-bg-color:var(--c-primary-hover);
  --el-button-hover-border-color:var(--c-primary-hover);
}
.el-button--default {
  --el-button-bg-color:#fff;
  --el-button-border-color:var(--c-border);
  --el-button-hover-border-color:var(--c-border-hover);
  --el-button-hover-text-color:var(--c-text);
}

.el-input__wrapper {
  border-radius:var(--radius-sm) !important;
  box-shadow:0 0 0 1px var(--c-border) !important;
  transition:all var(--transition);
  background:#f9fafb;
}
.el-input__wrapper:hover { box-shadow:0 0 0 1px var(--c-border-hover) !important; background:#fff; }
.el-input.is-focus .el-input__wrapper { box-shadow:0 0 0 1px var(--c-primary) !important, 0 0 0 3px rgba(92,108,240,0.1) !important; background:#fff; }

.el-select .el-input__wrapper { border-radius:var(--radius-sm) !important; }

.el-tag { border-radius:6px; font-weight:500; padding:0 10px; height:24px; line-height:24px; font-size:12px; border:none; }
.el-tag--small { height:22px; line-height:22px; font-size:11px; }
.el-tag--primary.el-tag--light { background:var(--c-primary-light); color:var(--c-primary); }
.el-tag--success.el-tag--light { background:var(--c-success-light); color:var(--c-success); }
.el-tag--warning.el-tag--light { background:var(--c-warning-light); color:#b45309; }
.el-tag--danger.el-tag--light { background:var(--c-danger-light); color:var(--c-danger); }
.el-tag--info.el-tag--light { background:var(--c-info-light); color:var(--c-info); }

.el-dialog { border-radius:var(--radius-lg); box-shadow:var(--shadow-lg); border:1px solid var(--c-border); }
.el-dialog__header { padding:24px 28px 0; border:none; }
.el-dialog__headerbtn { top:24px; right:24px; }
.el-dialog__title { font-size:17px; font-weight:700; color:var(--c-text); }
.el-dialog__body { padding:20px 28px; }
.el-dialog__footer { padding:0 28px 24px; }

.el-radio-button__inner {
  border-radius:var(--radius-sm) !important; border:1px solid var(--c-border) !important;
  background:#fff; color:var(--c-text-secondary); font-size:12px; font-weight:500;
  padding:7px 14px; box-shadow:none; transition:all var(--transition);
}
.el-radio-button__original-radio:checked+.el-radio-button__inner {
  background:var(--c-primary); border-color:var(--c-primary) !important; color:#fff;
}

.el-switch { --el-switch-on-color:var(--c-primary); }

.el-empty__description { color:var(--c-text-muted); font-size:13px; }

.el-message { border-radius:var(--radius); box-shadow:var(--shadow-lg); border:1px solid var(--c-border); }

.el-tree-select__popper .el-tree-node__content { height:36px; border-radius:6px; }

.status-dot { display:inline-block; width:7px; height:7px; border-radius:50%; margin-right:6px; }
.status-dot.online { background:var(--c-success); }
.status-dot.offline { background:var(--c-text-muted); }
</style>
