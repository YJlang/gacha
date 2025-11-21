import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import MapImage from '../assets/map.svg';
import PixelButton from '../components/PixelButton';
import BlueCapsuleImage from '../assets/blue-capsule.svg';
import GachaMachineImage from '../assets/gacha-machine.svg';
import Village from '../components/Village';
import VillageModal from '../components/VillageModal';
import client from '../api/client';

export default function Villages() {
  const navigate = useNavigate();
  const [villages, setVillages] = useState([]);
  const [pageData, setPageData] = useState({ totalPages: 0, currentPage: 0 });
  const [filters, setFilters] = useState({ region: '', programType: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedVillageDetails, setSelectedVillageDetails] = useState(null);

  useEffect(() => {
    const fetchVillages = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await client.get('/villages', {
          params: {
            page: pageData.currentPage,
            size: 20,
            region: filters.region || undefined,
            programType: filters.programType || undefined,
          },
        });
        const { content, totalPages, currentPage } = response.data.data;
        setVillages(content);
        setPageData({ totalPages, currentPage });
      } catch (err) {
        setError(err.response?.data?.message || '마을 목록을 불러오는 데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchVillages();
  }, [pageData.currentPage, filters]);


  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
    setPageData(prev => ({ ...prev, currentPage: 0 })); // 필터 변경 시 첫 페이지로
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < pageData.totalPages) {
      setPageData(prev => ({ ...prev, currentPage: newPage }));
    }
  };

  const openModal = async (villageId) => {
    try {
      const response = await client.get(`/villages/${villageId}`);
      setSelectedVillageDetails(response.data.data);
      setIsModalOpen(true);
    } catch (err) {
      alert('상세 정보를 불러오는 데 실패했습니다.');
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedVillageDetails(null);
  };

  // 필터
  const regions = ['인천광역시', '경기도', '강원특별자치도', '충청북도', '충청남도', '전북특별자치도', '전라남도', '경상북도', '경상남도', '제주특별자치도'];
  const programTypes = ['농작물경작체험', '전통문화체험', '자연생태체험', '만들기체험', '건강', '기타'];

  return (
    <Container>
      <BackgroundMap src={MapImage} alt="Map Background" />

      <PixelButton
        text="마을 리스트"
        isButton={false}
      />

      <BannerCard onClick={() => navigate('/app/gacha')}>
        <BannerContent>
          <BannerTextArea>
            <BannerTitleContainer>
              <BannerBlueCapsuleImage src={BlueCapsuleImage} alt="Blue Capsule" />
              <BannerTitle>랜덤 여행지 뽑기</BannerTitle>
            </BannerTitleContainer>
            <BannerSubtitle>
              어디로 갈지 고민된다면?
              <br />
              운명에 맡겨보세요?
            </BannerSubtitle>
          </BannerTextArea>
          <BannerImage src={GachaMachineImage} alt="Gacha Machine" />
        </BannerContent>
      </BannerCard>

      {loading && <StatusText>로딩 중...</StatusText>}
      {error && <ErrorText>{error}</ErrorText>}

      <FilterContainer>
        <Select name="region" value={filters.region} onChange={handleFilterChange}>
          <option value="">전체 지역</option>
          {regions.map(r => <option key={r} value={r}>{r}</option>)}
        </Select>
        <Select name="programType" value={filters.programType} onChange={handleFilterChange}>
          <option value="">전체 프로그램</option>
          {programTypes.map(p => <option key={p} value={p}>{p}</option>)}
        </Select>
      </FilterContainer>

      <VillageList>
        {villages.map((village) => (
          <Village
            key={village.villageId}
            villageName={village.villageName}
            sidoName={village.sidoName}
            onClick={() => openModal(village.villageId)}
          />
        ))}

        <PaginationContainer>
          <PageButton onClick={() => handlePageChange(pageData.currentPage - 1)} disabled={pageData.currentPage === 0}>
            이전
          </PageButton>
          <span>페이지 {pageData.currentPage + 1} / {pageData.totalPages}</span>
          <PageButton onClick={() => handlePageChange(pageData.currentPage + 1)} disabled={pageData.currentPage + 1 >= pageData.totalPages}>
            다음
          </PageButton>
        </PaginationContainer>
      </VillageList>

      {selectedVillageDetails && (
        <VillageModal
          isOpen={isModalOpen}
          onClose={closeModal}
          villageName={selectedVillageDetails.villageName}
          address={selectedVillageDetails.address}
          programName={selectedVillageDetails.programName}
          programContent={selectedVillageDetails.programContent}
        />
      )}
    </Container>
  );
}

const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  background-color: #A0E4FF;
  padding: 60px 10px 45px;
  gap: 20px;
  position: relative;
  overflow: hidden;
`;

const BackgroundMap = styled.img`
  position: fixed;
  top: 170px;
  width: 360px;
  object-fit:cover;
  z-index: 0;
  pointer-events: none;
`;

const BannerCard = styled.div`
  width: 100%;
  max-width: 375px;
  height: 120px;
  flex-shrink: 0;
  background: linear-gradient(225deg, #BD2469 0%, #8D1FA6 100%);
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.2s;
  overflow: hidden;
  position: relative;

  &:hover {
    transform: scale(1.02);
  }
`;

const BannerContent = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
`;

const BannerTextArea = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
`;

const BannerTitleContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
`;

const BannerBlueCapsuleImage = styled.img`
  width: 45px;
`;

const BannerTitle = styled.h2`
  font-size: 20px;
  color: white;
  white-space: nowrap;
`;

const BannerSubtitle = styled.p`
  font-size: 14px;
  color: white;
  line-height: 1.4;
  text-align: left;
`;

const BannerImage = styled.img`
  height: 100px;
`;

const FilterContainer = styled.div`
  display: flex;
  justify-content: center;
  gap: 20px;
  z-index: 1;
`;

const Select = styled.select`
  font-family: inherit;
  font-size: 16px;
  padding: 10px 15px;
  border-radius: 8px;
  border: 2px solid #D4D4D4;
`;

const StatusText = styled.p`
  font-size: 24px;
  color: white;
`;

const ErrorText = styled.p`
  font-size: 24px;
  color: red;
`;

const VillageList = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  overflow-y: auto;

  &::-webkit-scrollbar {
    display: none;
  }
  -ms-overflow-style: none;
  scrollbar-width: none;
`;

const PaginationContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  z-index: 1;

  span {
    font-size: 16px;
  }
`;

const PageButton = styled.button`
  font-family: inherit;
  font-size: 16px;
  padding: 8px 16px;
  border-radius: 5px;
  border: none;
  background-color: #8D1FA6;
  color: white;
  cursor: pointer;

  &:disabled {
    background-color: #D4D4D4;
    cursor: not-allowed;
  }
`;