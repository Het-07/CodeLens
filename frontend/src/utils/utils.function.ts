export const storeKey = (key: string,value: any) => {
    localStorage.setItem(key, value);
}

export const getKey = (key: string) => {
    return localStorage.getItem(key);
}