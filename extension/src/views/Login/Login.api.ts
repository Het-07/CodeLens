import { CONFIG } from "../../config/dev.config";
import { LogUsers } from "./Login.interface";

export const LoginUser = async (payload: LogUsers) => {
    try {
        const response = await fetch(`${CONFIG.BASE_URL}/auth/authenticate`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });

        if (!response.ok) { // Check for HTTP errors
            const errorData = await response.json();
            throw new Error(errorData.message || 'Login failed');
        }

        return await response.json(); // Success case
    } catch (error) {
        console.error('LoginUser error:', error); // Log the error
        throw error; // Re-throw for the caller to handle
    }
};