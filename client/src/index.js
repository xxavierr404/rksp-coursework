import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import MainPage from "./components/MainPage";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage";
import Profile from "./components/Profile";
import ChatList from "./components/ChatList";
import RegisterEmployee from "./components/RegisterEmployee";
import ChatStompContextWrapper from "./components/ChatStompContextWrapper";

const router = createBrowserRouter([
    {
        path: "/",
        element: <MainPage></MainPage>,
    },
    {
        path: "/login",
        element: <LoginPage></LoginPage>
    },
    {
        path: "/register",
        element: <RegisterPage></RegisterPage>
    },
    {
        path: "/profile",
        element: <Profile></Profile>
    },
    {
        path: "/profile/:id",
        element: <Profile></Profile>
    },
    {
        path: "/chats",
        element: <ChatList></ChatList>
    },
    {
        path: "/register/employee",
        element: <RegisterEmployee></RegisterEmployee>
    },
    {
        path: "/chat/:id",
        element: <ChatStompContextWrapper></ChatStompContextWrapper>
    }
]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <RouterProvider router={router} />
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
