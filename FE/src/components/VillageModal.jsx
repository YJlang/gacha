import React from "react";
import styled from 'styled-components';
import modalXBtn from '../assets/ModalXBtn.svg'
import location from '../assets/Location.svg'
import PixelButton from "./PixelButton";

export default function VillageModal({
  isOpen = false,
  onClose,
  villageName,
  address,
  programName,
  programContent,
  imageUrl,
  isButton = false,
  buttonText,
  buttonOnClick,
}) {
  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose?.();
    }
  };

  return (
    <ModalOverlay $isOpen={isOpen} onClick={handleOverlayClick}>
      <ModalContainer $isOpen={isOpen}>
        <ModalXBtn src={modalXBtn} alt='나가기' onClick={onClose} />

        <ImageSection>
          {imageUrl ? (
            <img src={imageUrl} alt={villageName} />
          ) : (
            "사진"
          )}
        </ImageSection>

        <InfoSection>
          <Title>{villageName}</Title>

          <Address>
            <AddressImg src={location} alt='위치표시' />
            {address}
          </Address>

          <Divider />

          <Section>
            <SectionTitle>{programName}</SectionTitle>
            <Content>{programContent}</Content>
          </Section>

          {isButton && (
            <ButtonContainer>
              <PixelButton
                text={buttonText}
                onClick={buttonOnClick}
                backgroundColor="#DD1A21"
                textColor="white"
                borderColor="#05131D"
                width="100px"
                fontSize="16px"
              />
            </ButtonContainer>
          )}
        </InfoSection>
      </ModalContainer>
    </ModalOverlay>
  );
}

const ModalOverlay = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  opacity: ${props => props.$isOpen ? 1 : 0};
  pointer-events: ${props => props.$isOpen ? 'all' : 'none'};
  transition: opacity 0.3s ease;
`;

const ModalContainer = styled.div`
  background: #D4D4D4;
  border-radius: 10px;
  width: 100%;
  max-width: 380px;
  max-height: 90vh;
  overflow: hidden;
  position: relative;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  transform: ${props => props.$isOpen ? 'scale(1)' : 'scale(0.9)'};
  transition: transform 0.3s ease;
`;

const ModalXBtn = styled.img`
  position: absolute;
  top: 16px;
  right: 16px;
  width: 30px;
  height: 30px;
  cursor: pointer;
  z-index: 10;
`;

const ImageSection = styled.div`
  width: 100%;
  height: 280px;
  background: linear-gradient(135deg, #B8B8B8 0%, #A0A0A0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: inherit;
  font-size: 34px;
  color: rgba(0, 0, 0, 0.3);
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
`;

const InfoSection = styled.div`
  background: white;
  padding: 24px;
  ${props => !props.isButton && 'padding-bottom: 40px;'}
  border-radius: 0 0 10px 10px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 16px;
`;

const Title = styled.h2`
  font-size: 30px;
  color: black;
`;

const Address = styled.div`
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 20px;
  text-align: left;
  color: black;
`;

const AddressImg = styled.img`
  height:20px;
`;

const Divider = styled.div`
  width: 100%;
  height: 1px;
  background: #E0E0E0;
`;

const Section = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  color: black;
`;

const SectionTitle = styled.h3`
  font-size: 16px;
  text-align: left;
`;

const Content = styled.span`
  font-size: 12px;
  text-align: left;
`;

const ButtonContainer = styled.div`
  width:100%;
  display: flex;
  justify-content: flex-end;
`;
