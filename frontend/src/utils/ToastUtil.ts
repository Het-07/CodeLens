// src/utils/toastUtils.js
export const showToast = (toastRef: any, severity: string, summary: string, detail: string, life = 3000) => {
    if (toastRef && toastRef.current) {
        toastRef.current.show({
            severity,
            summary,
            detail,
            life,
        });
    }
};