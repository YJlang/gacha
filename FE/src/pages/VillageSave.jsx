import styled from 'styled-components';
import vSBackground from '../assets/VSBackground.svg'
import Village from '../components/Village';
import saveT from '../assets/SaveT.svg';
import VillageModal from "../components/villageSave/VillageModal";
import MemoryAdd from '../components/villageSave/MemoryAdd';
import { useState, useEffect } from "react";
import client from '../api/client';

function VillageSave() {

    const [villageList, setVillageList] = useState([]);
    const [pageData, setPageData] = useState({ totalPages: 0, currentPage: 0 });

    useEffect(() => {
        const fetchCollections = async () => {
            try {
                const res = await client.get('/collections', {
                    params: {
                        page: pageData.currentPage,
                        size: 20,
                    }
                });

                const { content, totalPages, currentPage } = res.data.data;

                if (res.data.success) {
                    setVillageList(content);
                    setPageData({ totalPages, currentPage });
                }
            } catch (error) {
                console.error("collection 불러오기 실패:", error);
            }
        };
        fetchCollections();
    }, [pageData.currentPage]);

    //세부 정보 확인 모달
    const [modalOpen, setModalOpen] = useState(false);
    const [villageNum, setVillageNum] = useState();


    const clickVillage = (id) => {
        setModalOpen(true);
        setVillageNum(id)
        console.log('모달 작동')
    }

    const closeModal = () => {
        setModalOpen(false);
    }


    //추억 저장 모달 코드
    //모달 코드
    const [MmodalOpen, setMModalOpen] = useState(false);

    const clickWriteBtn = () => {
        setMModalOpen(true);
        setModalOpen(false);
    }

    const closeMModal = () => {
        setMModalOpen(false);
    }

    //페이지 이동

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < pageData.totalPages) {
            setPageData(prev => ({ ...prev, currentPage: newPage }));
        }
    };

    const filteredData = villageList.filter((item) => item.collectionId === villageNum);

    return (
        <VSaveBasic>
            <SaveTImg src={saveT} alt="마을 저장소 제목" />
            <PaginationContainer>
                <PageButton onClick={() => handlePageChange(pageData.currentPage - 1)} disabled={pageData.currentPage === 0}>
                    이전
                </PageButton>
                <span>페이지 {pageData.currentPage + 1} / {pageData.totalPages}</span>
                <PageButton onClick={() => handlePageChange(pageData.currentPage + 1)} disabled={pageData.currentPage + 1 >= pageData.totalPages}>
                    다음
                </PageButton>
            </PaginationContainer>
            <section>
                {villageList.map((village) => {
                    return (
                        <Village key={village.collectionId}{...village} onClick={() => clickVillage(village.collectionId)} />
                    );
                })}
                {modalOpen && (
                    <VillageModal onClick={(e) => e.stopPropagation()} closeModal={closeModal} clickWriteBtn={clickWriteBtn} key={filteredData[0].collectionId}{...filteredData[0]} />
                )}
                {MmodalOpen && (
                    <MemoryAdd onClick={(e) => e.stopPropagation()} closeMModal={closeMModal} key={filteredData[0].collectionId}{...filteredData[0]} />
                )}
            </section>
        </VSaveBasic>
    );
}

const VSaveBasic = styled.div`
margin:auto;
display: flex;
width: 393px;
height: 852px;
flex-direction: column;
align-items: center;
position: relative;
background: #FFADAE url(${vSBackground}) no-repeat center;
section{
margin-top: 10px;
display:flex;
flex-direction:column;
width: 375px;
gap:10px; 
overflow-y: auto;
padding-bottom: 90px;
-ms-overflow-style: none;
}

section::-webkit-scrollbar{
  display:none;
}

`;

const SaveTImg = styled.img`
  margin-top: 23px;
  width: 212px;
  height: 127px;
`;

const PaginationContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;

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

export default VillageSave;