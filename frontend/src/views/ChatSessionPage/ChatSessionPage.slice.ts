import { createSlice } from "@reduxjs/toolkit";
import { RootState } from "../../redux/store";
import { DEFAULT_SESSION } from "../SidebarLayout/SidebarLayout.constants";
import { IChatSessionInitialState } from "./ChatSessionPage.interface";

const initialState: IChatSessionInitialState = {
    sessionLoadingStates: new Map(), // Use a Map
    selectedSession: DEFAULT_SESSION,
    allSessions: [],
    isCodeModeEnabled: false
};

const chatSessionSlice = createSlice({
    name: 'sessions', 
    initialState,
    reducers: {
      setSessionLoading: (state, action) => {
        const { sessionId, isLoading } = action.payload;
        state.sessionLoadingStates.set(sessionId, isLoading);
      },
      clearSessionLoading: (state, action) => {
        const { sessionId } = action.payload;
        state.sessionLoadingStates.delete(sessionId);
      },
      resetSessionLoading: (state) => {
        state.sessionLoadingStates.clear();
      },
      setAllSessionLoading: (state, action) => {
        const { sessionLoadingState } = action.payload;
        state.sessionLoadingStates = sessionLoadingState
      },
      setCurrentSession: (state, action) => {
        const {session} = action.payload;
        state.selectedSession = session;
      },
      setAllSessions: (state, action) => {
        const {allSessions} = action.payload;
        state.allSessions = allSessions;
      },
      setState: (state, payload) => {
        const { key, value } = payload.payload;
        state[key as keyof IChatSessionInitialState] = value;
    },
    },
  });  

  export const { setSessionLoading, clearSessionLoading, resetSessionLoading, setAllSessionLoading, setCurrentSession, setAllSessions, setState } = chatSessionSlice.actions;

export const sessionState = (
    state: RootState
) => state.session

export default chatSessionSlice.reducer;