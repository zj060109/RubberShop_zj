<template>
  <router-view />
</template>

<style>
:root {
  --primary: #6366f1;
  --primary-light: #818cf8;
  --primary-dark: #4f46e5;
  --primary-bg: #eef2ff;
  --success: #10b981;
  --success-bg: #d1fae5;
  --warning: #f59e0b;
  --warning-bg: #fef3c7;
  --danger: #ef4444;
  --danger-bg: #fee2e2;
  --info: #3b82f6;
  --info-bg: #dbeafe;
  --bg: #f0f2f5;
  --bg-card: #ffffff;
  --text: #1e293b;
  --text-secondary: #64748b;
  --text-muted: #94a3b8;
  --border: #e2e8f0;
  --border-light: #f1f5f9;
  --shadow-sm: 0 1px 2px rgba(0,0,0,0.04), 0 1px 3px rgba(0,0,0,0.06);
  --shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.06);
  --shadow-md: 0 4px 6px rgba(0,0,0,0.04), 0 8px 24px rgba(0,0,0,0.08);
  --radius: 10px;
  --radius-sm: 8px;
  --transition: 0.2s cubic-bezier(0.4,0,0.2,1);
}

* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { height: 100%; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif; color: var(--text); }

::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
::-webkit-scrollbar-track { background: transparent; }

.page-container {
  padding: 24px;
  background: var(--bg);
  min-height: calc(100vh - 56px);
}

.card-toolbar {
  background: var(--bg-card);
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
  border: 1px solid var(--border);
}

.card-table {
  background: var(--bg-card);
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border);
}

.card-table .el-card__body { padding: 0; }

.el-card {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition);
}

.el-button--primary {
  --el-button-bg-color: var(--primary);
  --el-button-border-color: var(--primary);
  --el-button-hover-bg-color: var(--primary-dark);
  --el-button-hover-border-color: var(--primary-dark);
  --el-button-active-bg-color: var(--primary-dark);
  --el-button-active-border-color: var(--primary-dark);
  font-weight: 500;
  border-radius: var(--radius-sm);
}

.el-input__wrapper {
  border-radius: var(--radius-sm) !important;
  transition: all var(--transition);
  box-shadow: 0 0 0 1px var(--border) !important;
}
.el-input__wrapper:hover { box-shadow: 0 0 0 1px var(--primary-light) !important; }
.el-input.is-focus .el-input__wrapper,
.el-input__wrapper.is-focus { box-shadow: 0 0 0 1px var(--primary) !important, 0 0 0 3px rgba(99,102,241,0.12) !important; }

.el-select .el-input__wrapper { border-radius: var(--radius-sm) !important; }

.el-table { --el-table-header-bg-color: #f8fafc; }
.el-table th.el-table__cell { background: #f8fafc; color: var(--text-secondary); font-weight: 600; font-size: 13px; letter-spacing: 0.01em; border-bottom: 2px solid var(--border); }
.el-table tr.el-table__row:hover > td.el-table__cell { background: #f8fafc; }
.el-table td.el-table__cell { border-color: var(--border-light); }

.el-pagination { --el-pagination-button-bg-color: #fff; }
.el-pagination.is-background .el-pager li { border-radius: 6px; border: 1px solid var(--border); }
.el-pagination.is-background .el-pager li.is-active { background: var(--primary); border-color: var(--primary); color: #fff; }

.el-radio-button__inner { border-radius: 6px !important; border: 1px solid var(--border) !important; background: #fff; color: var(--text-secondary); padding: 6px 16px; font-size: 13px; transition: all var(--transition); }
.el-radio-button__original-radio:checked + .el-radio-button__inner { background: var(--primary); border-color: var(--primary) !important; color: #fff; box-shadow: none; font-weight: 500; }

.el-tag { border-radius: 6px; padding: 0 10px; font-weight: 500; }
.el-tag--small { font-size: 12px; height: 22px; line-height: 22px; }

.el-tag--primary.el-tag--light { background: var(--primary-bg); border-color: #c7d2fe; color: var(--primary-dark); }
.el-tag--success.el-tag--light { background: var(--success-bg); border-color: #a7f3d0; color: #065f46; }
.el-tag--warning.el-tag--light { background: var(--warning-bg); border-color: #fde68a; color: #92400e; }
.el-tag--danger.el-tag--light { background: var(--danger-bg); border-color: #fecaca; color: #b91c1c; }
.el-tag--info.el-tag--light { background: var(--info-bg); border-color: #bfdbfe; color: #1e40af; }

.el-dialog { border-radius: 12px; }
.el-dialog__header { padding: 20px 24px 12px; border-bottom: 1px solid var(--border-light); margin: 0; }
.el-dialog__headerbtn { top: 20px; right: 20px; }
.el-dialog__title { font-size: 16px; font-weight: 600; color: var(--text); }
.el-dialog__body { padding: 20px 24px; }
.el-dialog__footer { padding: 12px 24px 20px; }

.el-descriptions__label { font-weight: 500; color: var(--text-secondary); background: #f8fafc; }
.el-descriptions__content { color: var(--text); }

.el-message { border-radius: var(--radius-sm); box-shadow: var(--shadow); }

.el-button--text, .el-button.is-text { color: var(--primary); }

.el-switch { --el-switch-on-color: var(--primary); }

.loading-text { color: var(--text-muted); text-align: center; padding: 48px 0; font-size: 14px; }
</style>
