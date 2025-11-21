import styled from 'styled-components';
import mPlusBtn from '../assets/MPlusBtn.svg';
import { useEffect, useState } from 'react';
import { useNavigate } from "react-router-dom";
import MemoryCard from '../components/myMemory/MemoryCard';
import myMemoryT from '../assets/MyMemoryT.svg';
import client from '../api/client';

//swiper 라이브러리 설치했습니다. npm install swiper
import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css';
import 'swiper/css/pagination';


function MyMemory() {

    const [memoryList, setMemoryList] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchMemories = async () => {
            try {
                const res = await client.get('/memories', {
                    params: {
                        page: 0,
                        size: 20,
                    }
                });

                if (res.data.success) {
                    setMemoryList(res.data.data.content);
                }
            } catch (error) {
                console.log('memory 불러오기 실패:', error)
            }
        };

        fetchMemories();
    }, []);


    // 더미데이터


    //슬라이딩 코드
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
            <div>
                <Swiper {...swiperConfig} >

                    {memoryList.map((memory) => (
                        <SwiperSlide>
                            <MemoryCard key={memory.collectionId}{...memory} />
                        </SwiperSlide>
                    ))}

                </Swiper>
            </div>
            <img src={mPlusBtn} alt='메모리 추가' onClick={() => navigate("/app/village/save")} />
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
  justify-content: center; 카드를 세로 중앙에 배치
  position: relative;
  background-color: #BEFFAD;
  overflow: hidden;

  >img{
    position: absolute;
    bottom: 106px;
    right:20px;
    }

  .swiper-container {
    width: 100%;
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

const MemoryTImg = styled.img`
  width: 212px;
  height: 127px;
  position: absolute;
  top:23px;
  left:91px;
`;




export default MyMemory;