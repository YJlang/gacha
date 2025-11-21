import React from "react";
import styled from "styled-components";

export default function PixelButton({
  text,
  onClick,
  backgroundColor = "white",
  textColor = "black",
  borderColor = "#1E3445",
  width = "212px",
  fontSize = "32px",
  isButton = true,
  className
}) {
  const clickable = isButton && onClick;

  return (
    <ButtonContainer
      onClick={clickable ? onClick : undefined}
      $bgColor={backgroundColor}
      $textColor={textColor}
      $borderColor={borderColor}
      $width={width}
      $fontSize={fontSize}
      $clickable={clickable}
      className={className}
    >
      <BorderTop $width={width}>
        <BorderTopSvgWrapper>
          <svg
            style={{ display: "block", width: "100%", height: "100%" }}
            fill="none"
            preserveAspectRatio="none"
            viewBox="0 0 212 9"
          >
            <g id="border-top">
              <g id="Union (Stroke)">
                <path d="M2.99541 6V3H5.99541V0H8.99541V3V6H5.99541V9H2.99541V6Z" fill={borderColor} />
                <path d="M2.99541 6V9H9.53674e-07V6H2.99541Z" fill={borderColor} />
              </g>
              <rect fill={borderColor} height="3" id="Rectangle 1134" width="194.009" x="8.99541" />
              <g id="Union (Stroke)_2">
                <path d="M209.005 6V3H206.005V0H203.005V3V6H206.005V9H209.005V6Z" fill={borderColor} />
                <path d="M209.005 6V9H212V6H209.005Z" fill={borderColor} />
              </g>
            </g>
          </svg>
        </BorderTopSvgWrapper>
      </BorderTop>

      <Mid $width={width}>
        <BorderSideWrapper>
          <BorderSide>
            <BorderSideInner $borderColor={borderColor} />
          </BorderSide>
        </BorderSideWrapper>

        <Content $bgColor={backgroundColor}>
          <TextContainer>
            <TextInner>
              <TextContent>
                <ButtonText $textColor={textColor} $fontSize={fontSize} $width={width}>
                  {text}
                </ButtonText>
              </TextContent>
            </TextInner>
          </TextContainer>
        </Content>

        <BorderSideWrapper>
          <BorderSide>
            <BorderSideInner $borderColor={borderColor} />
          </BorderSide>
        </BorderSideWrapper>

        <CornerRight>
          <svg
            style={{ display: "block", width: "100%", height: "100%" }}
            fill="none"
            preserveAspectRatio="none"
            viewBox="0 0 6 6"
          >
            <path d="M6 0V3V6H3V3H0V0H3H6Z" fill={borderColor} id="corner-right" />
          </svg>
        </CornerRight>

        <CornerLeft>
          <CornerLeftInner>
            <CornerLeftContent>
              <svg
                style={{ display: "block", width: "100%", height: "100%" }}
                fill="none"
                preserveAspectRatio="none"
                viewBox="0 0 6 6"
              >
                <path d="M6 0V3V6H3V3H0V0H3H6Z" fill={borderColor} id="corner-left" />
              </svg>
            </CornerLeftContent>
          </CornerLeftInner>
        </CornerLeft>
      </Mid>

      <BottomBorderWrapper>
        <BottomBorderInner>
          <BorderBtm $width={width}>
            <BorderBtmSvgWrapper>
              <svg
                style={{ display: "block", width: "100%", height: "100%" }}
                fill="none"
                preserveAspectRatio="none"
                viewBox="0 0 212 9"
              >
                <g id="border-btm">
                  <g id="Union (Stroke)">
                    <path d="M2.99541 6V3H5.99541V0H8.99541V3V6H5.99541V9H2.99541V6Z" fill={borderColor} />
                    <path d="M2.99541 6V9H0V6H2.99541Z" fill={borderColor} />
                  </g>
                  <rect fill={borderColor} height="3" id="Rectangle 1134" width="194.009" x="8.99541" />
                  <g id="Union (Stroke)_2">
                    <path d="M209.005 6V3H206.005V0H203.005V3V6H206.005V9H209.005V6Z" fill={borderColor} />
                    <path d="M209.005 6V9H212V6H209.005Z" fill={borderColor} />
                  </g>
                </g>
              </svg>
            </BorderBtmSvgWrapper>
          </BorderBtm>
        </BottomBorderInner>
      </BottomBorderWrapper>
    </ButtonContainer>
  );
}

const ButtonContainer = styled.div`
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  position: relative;
  width: ${props => props.$width || '212px'};
  cursor: ${props => props.$clickable ? 'pointer' : 'default'};
  transition: transform 0.1s ease;

  ${props => props.$clickable && `
    &:hover {
      transform: translate(1px, 1px);
    }

    &:active {
      transform: translate(3px, 3px);
    }
  `}
`;

const BorderTop = styled.div`
  height: 3px;
  position: relative;
  flex-shrink: 0;
  width: ${props => props.$width || '212px'};
`;

const BorderTopSvgWrapper = styled.div`
  position: absolute;
  bottom: -200%;
  left: 0;
  right: 0;
  top: 0;
`;

const BorderBtm = styled.div`
  height: 3px;
  position: relative;
  width: ${props => props.$width || '212px'};
`;

const BorderBtmSvgWrapper = styled.div`
  position: absolute;
  bottom: -200%;
  left: 0;
  right: 0;
  top: 0;
`;

const Mid = styled.div`
  display: flex;
  align-items: center;
  position: relative;
  flex-shrink: 0;
  width: ${props => props.$width || '212px'};
`;

const BorderSide = styled.div`
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  height: 100%;
  align-items: flex-start;
  padding: 6px 0;
  position: relative;
  flex-shrink: 0;
`;

const BorderSideInner = styled.div`
  flex-basis: 0;
  background-color: ${props => props.$borderColor || '#1e3445'};
  flex-grow: 1;
  min-height: 1px;
  min-width: 1px;
  flex-shrink: 0;
  width: 3px;
`;

const Content = styled.div`
  flex-basis: 0;
  background-color: ${props => props.$bgColor || 'white'};
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex-grow: 1;
  align-items: flex-start;
  min-height: 1px;
  min-width: 1px;
  padding: 12px 0;
  position: relative;
  flex-shrink: 0;
`;

const TextContainer = styled.div`
  position: relative;
  flex-shrink: 0;
  width: 100%;
`;

const TextInner = styled.div`
  width: 100%;
  height: 100%;
`;

const TextContent = styled.div`
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-start;
  padding: 0 10px;
  position: relative;
  width: 100%;
`;

const ButtonText = styled.p`
  line-height: 1.3;
  position: relative;
  flex-shrink: 0;
  font-size: ${props => props.$fontSize || '32px'};
  color: ${props => props.$textColor || 'black'};
  text-align: center;
  letter-spacing: 0.5px;
  width: calc(${props => props.$width || '212px'} - 26px);
  margin: 0;
`;

const CornerRight = styled.div`
  position: absolute;
  right: 3px;
  width: 6px;
  height: 6px;
  top: 0;
`;

const CornerLeft = styled.div`
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  left: 3px;
  width: 6px;
  height: 6px;
  top: 0;
`;

const CornerLeftInner = styled.div`
  flex: none;
  transform: rotate(180deg) scaleY(-1);
`;

const CornerLeftContent = styled.div`
  position: relative;
  width: 6px;
  height: 6px;
`;

const BorderSideWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  align-self: stretch;
`;

const BottomBorderWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
`;

const BottomBorderInner = styled.div`
  flex: none;
  transform: scaleY(-1);
`;
