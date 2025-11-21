import React, { useState, useEffect } from "react";
import styled from "styled-components";
import PixelButton from "../components/PixelButton";
import TrophyImg from "../assets/trophy.svg";
import GachaMachineImage from "../assets/gacha-machine.svg";
import client from "../api/client";
import VillageModal from "../components/VillageModal";

export default function Gacha() {
  const [collections, setCollections] = useState(null);
  const [gachaStatus, setGachaStatus] = useState(null);
  const [gachaResult, setGachaResult] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const gachaResponse = await client.get("/gacha/status");
        setGachaStatus(gachaResponse.data.data);

        const collectionResponse = await client.get("/collections/stats");
        setCollections(collectionResponse.data.data);
      } catch (err) {
        setError(
          err.response?.data?.message || "데이터를 불러오는 데 실패했습니다."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleDraw = async () => {
    if (!gachaStatus?.canDraw) {
      alert("오늘은 이미 뽑기를 완료했습니다.");
      return;
    }

    setLoading(true);
    setGachaResult(null);
    setError(null);

    try {
      const response = await client.post("/gacha/draw", {});
      setGachaResult(response.data.data);
      setIsModalOpen(true);

      setGachaStatus((prev) => ({
        ...prev,
        canDraw: false,
        remainingCount: 0,
      }));
    } catch (err) {
      setError(
        err.response?.data?.message || "뽑기에 실패했습니다. 다시 시도해주세요."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCollection = async () => {
    if (!gachaResult) return;

    try {
      await client.post("/collections", {
        villageId: gachaResult.villageId,
      });
      alert("컬렉션에 추가되었습니다!");
      setIsModalOpen(false);

      if (gachaResult.isNew) {
        setCollections((prev) => ({
          ...prev,
          totalCount: (prev?.totalCount || 0) + 1,
        }));
      }
    } catch (err) {
      alert(err.response?.data?.message || "컬렉션 추가에 실패했습니다.");
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  return (
    <Container>
      <PixelButton
        text="마을 가챠!"
        isButton={false}
      />

      <CollectionText>
        <CollectionImage src={TrophyImg} alt="trophy" />
        내 컬렉션 ({collections?.totalCount || 0}/100)
      </CollectionText>

      <GachaImage src={GachaMachineImage} alt="Gacha Machine" />

      <PixelButton
        text={gachaStatus?.canDraw ? "랜덤 마을 뽑기" : "내일 다시 만나요!"}
        onClick={handleDraw}
        backgroundColor={gachaStatus?.canDraw ? '#DD1A21' : '#371ADD'}
        textColor='white'
        borderColor='#05131D'
        width='245px'
        fontSize='24px'
        isButton={!loading}
      />

      {error && <ErrorText>{error}</ErrorText>}

      {gachaResult && (
        <VillageModal
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          villageName={gachaResult.villageName}
          address={gachaResult.address}
          programName={gachaResult.programName}
          programContent={gachaResult.programContent}
          isButton={gachaResult.isNew}
          buttonText="저장하기"
          buttonOnClick={handleAddToCollection}
        />
      )}
    </Container>
  );
}

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: linear-gradient(225deg, #AF7EE7 0%, #BD2469 100%);
  padding: 40px;
  gap: 20px;
`;

const CollectionText = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 20px;
  color: white;
  text-shadow: 2px 2px 0px rgba(0, 0, 0, 0.3);
`;

const CollectionImage = styled.img`
  width: 24px;
`;

const GachaImage = styled.img`
  width: 300px;
  max-width: 90%;
`;

const ErrorText = styled.p`
  font-size: 16px;
  color: red;
`;
