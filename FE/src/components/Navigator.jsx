import React from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import nList from "../assets/NList.svg";
import nMemory from "../assets/NMemory.svg";
import nMy from "../assets/NMy.svg";
import nSave from "../assets/NSave.svg";
import gachaBtn from "../assets/GachaBtn.svg";

function Navigator() {
  const navigate = useNavigate();

  return (
    <NavigatorContainer>
      <img src={nList} alt="리스트" onClick={() => navigate("/app/village")} />
      <img src={nSave} alt="저장" onClick={() => navigate("/app/village/save")} />
      <GachaButton onClick={() => navigate("/app/gacha")}>
        <img src={gachaBtn} alt="가챠" />
      </GachaButton>
      <img src={nMemory} alt="기억" onClick={() => navigate("/app/mymemory")} />
      <img src={nMy} alt="마이" onClick={() => navigate("/app/mypage")} />
    </NavigatorContainer>
  );
}

const NavigatorContainer = styled.div`
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 72px;
  padding: 16px 22px;
  align-items: center;
  background-color: #fafafa;
  z-index: 100;
`;

const GachaButton = styled.button`
  display: flex;
  width: 70px;
  height: 70px;
  padding: 15px;
  justify-content: center;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
  border-radius: 35px;
  background: linear-gradient(233deg, #fa2283 9.8%, #8d1fa6 81.22%);
  box-shadow: 0 4px 4px 0 rgba(0, 0, 0, 0.25) inset;
  border: 0;
  position: absolute;
  bottom: 39px;
  left: 161.5px;
`;

export default Navigator;
