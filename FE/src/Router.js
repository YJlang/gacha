import React from "react";
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import PrivateRoute from "./components/PrivateRoute";
import Landing from "./pages/Landing";
import Auth from "./pages/Auth";
import Gacha from "./pages/Gacha";
import Villages from "./pages/Villages";
import VillageSave from "./pages/VillageSave";
import MyMemory from "./pages/MyMemory";
import MyPage from "./pages/MyPage";
import Navigator from "./components/Navigator";

const AppLayout = () => (
  <div style={{ paddingBottom: "72px", height: "100%" }}>
    <Routes>
      <Route path="gacha" element={<Gacha />} />
      <Route path="village" element={<Villages />} />
      <Route path="village/save" element={<VillageSave />} />
      <Route path="mymemory" element={<MyMemory />} />
      <Route path="mypage" element={<MyPage />} />

      <Route path="/" element={<Navigate to="village" replace />} />
    </Routes>
    <Navigator />
  </div>
);

function Router() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="auth" element={<Auth />} />

        <Route path="app/*" element={<PrivateRoute element={AppLayout} />} />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default Router;
