import React, { useState, useEffect } from 'react';
import styled, { keyframes, css } from 'styled-components';
import { useNavigate } from 'react-router-dom';
import PixelButton from '../components/PixelButton';
import landingGachaImage from '../assets/gacha-machine.svg';

const fadeIn = keyframes`
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
`;

const AnimatedPixelButton = styled(PixelButton)`
  opacity: 0;
  transition: opacity 0.5s ease, transform 0.5s ease;
  
  ${props => props.isVisible && css`
    opacity: 1;
    animation: ${fadeIn} 0.5s ease-out forwards;
  `}
`;

export default function Landing() {
  const navigate = useNavigate();
  const [showButtons, setShowButtons] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setShowButtons(true);
    }, 1000);
    return () => clearTimeout(timer);
  }, []);

  const handleLoginClick = () => {
    navigate('/auth');
  };

  return (
    <Container>
      <PixelButton
        text="여행 가챠!"
        isButton={false}
      />

      <GachaImage src={landingGachaImage} alt="Gacha Machine" />

      <AnimatedPixelButton
        text="로그인"
        onClick={handleLoginClick}
        backgroundColor='#DD1A21'
        textColor='white'
        borderColor='#05131D'
        width='210px'
        fontSize='24px'
        isVisible={showButtons}
      />
    </Container>
  );
};

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #A0E4FF;
  padding: 12px;
  gap: 60px;
`;

const GachaImage = styled.img`
  width: 370px;
  max-width: 90%;
`;
