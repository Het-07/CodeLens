import { CONFIG } from "../../config/dev.config";
import { IUser } from "./Register.interface";

export const createUser = async (payload: IUser) => {
    try {
        const user = await fetch(
            `${CONFIG.BASE_URL}/auth/register`,
            {
                method: 'POST',
                headers: {
                    "Content-type": "application/json",
                },
                body: JSON.stringify(payload)
            }
        )
        const json = await user.json();
        return json;
    } catch (error) {
        throw error;
    }
}