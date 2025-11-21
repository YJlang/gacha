import styled from 'styled-components';
import Navigator from '../navigator/Navigator';
import mPlusBtn from '../../img/MPlusBtn.svg';
import { useState } from 'react';
import MemoryAdd from './MemoryAdd';
import MemoryCard from './MemoryCard';
import myMemoryT from '../../img/MyMemoryT.svg'

//swiper 라이브러리 설치했습니다. npm install swiper
import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css';
import 'swiper/css/pagination';


function MyMemory() {
    //모달 코드
    const [modalOpen, setModalOpen] = useState(false);

    const clickPlusBtn = () => {
        setModalOpen(true);
        console.log('모달 작동')
    }

    const closeModal = () => {
        setModalOpen(false);
    }

    // 더미데이터
    const memorysList = [
        {
            "memoryId": 1,
            "villageId": 123,
            "villageName": "산청마을",
            "sidoName": "경상남도",
            "sigunguName": "산청군",
            "address": "경상남도 산청군 산청읍 산청리 123",
            "content": "정말 좋은 추억이었어요!",
            "visitDate": "2025-01-15",
            "createdAt": "2025-01-15T14:30:00"
        },
        {
            "memoryId": 2,
            "villageId": 124,
            "villageName": "하나마을",
            "sidoName": "경상남도",
            "sigunguName": "산청군",
            "address": "경상남도 산청군 산청읍 산청리 123",
            "content": "정말 좋은 추억이었어요!",
            "visitDate": "2025-09-15",
            "createdAt": "2025-01-15T14:30:00"
        },
        {
            "memoryId": 3,
            "villageId": 127,
            "villageName": "두리마을",
            "sidoName": "경상남도",
            "sigunguName": "산청군",
            "address": "경상남도 산청군 산청읍 산청리 123",
            "content": "정말 좋은 기억이었어요!",
            "visitDate": "2025-01-16",
            "createdAt": "2025-01-15T14:30:00"
        }

    ]

    const [memoryList, setMemoryList] = useState(memorysList);

    //슬라이딩 코드
    const [swiper, setSwiper] = useState(null);

    const swiperConfig = {
        slidesPerView: 'auto',
        spaceBetween: 25,
        centeredSlides: true,
        pagination: {
            clickable: true,
        },

    };

    return (
        <MMemoryBasic>
            <MemoryTImg src={myMemoryT} alt='메모리 제목' />
            {modalOpen && (
                <MemoryAdd onClick={(e) => e.stopPropagation()} closeModal={closeModal} />
            )}
            <div>
                <Swiper {...swiperConfig} onSwiper={setSwiper}>

                    {memoryList.map((memory) => (
                        <SwiperSlide>
                            <MemoryCard key={memory.collectionId}{...memory} />
                        </SwiperSlide>
                    ))}

                </Swiper>
            </div>
            <PlusBtn src={mPlusBtn} alt='메모리 추가' onClick={clickPlusBtn} />
            <Navigator />
        </MMemoryBasic>
    );
}

const MMemoryBasic = styled.div`
  margin: auto;
  display: flex;
  width: 393px;
  height: 852px;
  flex-direction: column;
  align-items: center;
  justify-content: center; /* 카드를 세로 중앙에 배치 */
  position: relative;
  background-color: #BEFFAD;
  overflow: hidden;

  /* 기존의 >img 선택자는 삭제하고 각각 별도 컴포넌트로 분리했습니다 */

  .swiper-container {
    width: 100%;
    /* 필요하다면 여기에 margin-bottom 등을 주어 카드 위치를 미세조정 */
  }

  .swiper {
    width: 300px;
    height: auto;
    overflow: visible;
  }

  .swiper-slide {
    width: 100%;
    display: flex;
    justify-content: center;
  }
`;

// 제목 이미지 (말풍선) 위치 수정
const MemoryTImg = styled.img`
  width: 212px;
  height: 127px;
  position: absolute;
  top:23px;
  left:91px;
`;

// 플러스 버튼 전용 스타일 생성
const PlusBtn = styled.img`
  position: absolute;
  bottom: 106px;
  right: 20px;
  cursor: pointer;
  z-index: 20; /* 제목보다 더 위에(클릭 가능하게) */
`;


export default MyMemory;