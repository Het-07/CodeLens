/* eslint-disable @typescript-eslint/no-explicit-any */
import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { IAuth } from "./Login.interface";
import { RootState } from "../../redux/store";
import { loginApi } from "./Login.api";

const userId = localStorage.getItem('userId');


const initialState: IAuth = {
    userId: userId ? userId : "",
    token: "",
    message: "",
    isError: false,
    isSuccess: false
}

export const getUserInfo = createAsyncThunk(
    "auth/getUserInfo",
    async ({ email, password }: { email: string, password: string },thunkAPI) => {
        try {
            return await loginApi({email, password});
        } catch (error: any) {            
            const errorMessage = error.response && error.response.data && error.response.data.err || "Invalid emailId or password";
            throw thunkAPI.rejectWithValue(errorMessage);
        }
    }
)

export const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        setInitialState: (state) => {
            state.token = ""
            state.userId = ""
        }
    },
    extraReducers(builder) {
        builder.addCase(getUserInfo.fulfilled, (state, action) => {
            const { token, user_details, message } = action.payload.body;
            state.token = token;
            state.userId = user_details.userId
            state.message = message
            localStorage.setItem("token",token);
            localStorage.setItem("userId", user_details.userId);
            state.isSuccess = true;
            state.isError = false
        })
        builder.addCase(getUserInfo.rejected, (state, action) => {
            state.isError = true
            state.message = action.payload as string
            state.isSuccess = false
        })
    },
})

export const authState = (
    state: RootState
): IAuth => state.auth;

export const { setInitialState } = authSlice.actions;

export default authSlice.reducer;