import { useEffect, useRef, useState } from "react"
import { ISession } from "../Dashboard.interface";
import { getAllUserSession } from "../Dashboard.api";
import { showToast } from "../../../utils/ToastUtil";
import { useDispatch } from "react-redux";
import { setAllSessionLoading, setAllSessions } from "../../ChatSessionPage/ChatSessionPage.slice";

export const useFetchUserSessions = (userId: string) => {
    const [sessions, setSessions] = useState<ISession[]>([]);
    const toast = useRef(null);
    const dispatch = useDispatch();
    
    useEffect(() => {
       const fetchUserSessions = async (userId: string) => {
            try {
                const response: ISession[] = await getAllUserSession(userId);
                const sessionMap = new Map();
                response.map(session => {
                    sessionMap.set(session, false);
                })
                dispatch(setAllSessionLoading({ sessionLoadingState: sessionMap }))
                dispatch(setAllSessions({allSessions: response}));
                setSessions(response);
            } catch (e) {
                const error = e as Error;
                showToast(toast, "error", "Error", error?.message);
            }
       }

       fetchUserSessions(userId);
    },[]);

    return { sessions, setSessions, toast };
}