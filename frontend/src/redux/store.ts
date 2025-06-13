import { Action, combineReducers, configureStore, ThunkAction } from "@reduxjs/toolkit";
import authReducer from '../views/Login/Login.slice';
import sessionReducer from '../views/ChatSessionPage/ChatSessionPage.slice';
import { persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage';

const sessionPersistConfig = {
  key: 'codelens:session',
  storage,
}

const persistConfig = {
  key: 'codelens',
  storage,
  whiteList: 'session'
}


const reducers = combineReducers({
    auth: authReducer,
    session: persistReducer(sessionPersistConfig, sessionReducer)
})

export const store = configureStore({
    reducer: persistReducer(persistConfig, reducers),
    middleware: (getDefaultMiddleware) => { return getDefaultMiddleware({ serializableCheck: false }); } 
})

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
export type AppThunk<ReturnType = void> = ThunkAction<
  ReturnType,
  RootState,
  unknown,
  Action<string>
>;