import styled from 'styled-components';
import location from '../assets/Location.svg'

function Village({ villageName, sidoName, imageUrl, onClick }) {
  // 회색 배경의 data URL (네트워크 요청 없이 즉시 표시)
  const defaultImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="375" height="145"%3E%3Crect width="375" height="145" fill="%23D4D4D4"/%3E%3Ctext x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" font-family="Arial" font-size="16" fill="%23666"%3ENo Image%3C/text%3E%3C/svg%3E';

  return (
    <VillageDiv onClick={onClick}>
      <ImgDiv
        src={imageUrl || defaultImage}
        alt={villageName}
        onError={(e) => { e.target.src = defaultImage; }}
      />
      <VillageInfo>
        <Title>{villageName}</Title>
        <Address>
          <AddressImg src={location} alt='위치표시' />
          {sidoName}
        </Address>
      </VillageInfo>
    </VillageDiv>
  );
}
const ImgDiv = styled.img`
height:145.77px;
width:375px;
position: absolute;
top:0;
left:0;
border-radius: 10px 10px 0 0;
object-fit: cover;
`

const VillageDiv = styled.div`
  width: 100%;
  height: 240px;
  flex-shrink: 0;
  border-radius: 10px;
  background-color: #D4D4D4;
  position: relative;
`

const VillageInfo = styled.div`
  position: absolute;
  bottom:0;
  width: 100%;
  height: 95px;
  flex-shrink: 0;
  border-radius: 0 0 10px 10px;
  background: white;
  padding: 13px;
`

const Title = styled.h2`
  font-size: 24px;
  color: black;
  margin-bottom: 10px;
  text-align: left;
`;

const Address = styled.div`
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 20px;
  color: black;
`;

const AddressImg = styled.img`
  height:20px;
`;

export default Village;