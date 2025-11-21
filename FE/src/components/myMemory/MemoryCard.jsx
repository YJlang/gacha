import styled from 'styled-components';
import location from '../../assets/Location.svg';
import calendar from '../../assets/calendar.svg';
import client from '../../api/client';
import { useState } from 'react';



function MemoryCard({ memoryId, villageName, sidoName, content, visitDate, imageUrl }) {
    const [deleteM, setDeleteM] = useState(false);

    const clickDelete = async () => {
        try {
            const res = await client.delete(`/memories/${memoryId}`);

            if (res.data.success) {
                window.location.reload();
            }
        } catch (error) {
            console.error("삭제 실패:", error);
            alert("삭제에 실패했습니다.")
        }
    }

    const clickDelteM = () => {
        setDeleteM(true);
    }

    const clickCloseDeleteM = () => {
        setDeleteM(false);
    }

    return (
        <Card>
            {deleteM && (
                <DeleteModal>
                    <p>삭제하시겠습니까?</p>
                    <div>
                        <button onClick={clickCloseDeleteM}>취소</button><button onClick={clickDelete}>삭제</button>
                    </div>
                </DeleteModal>
            )}
            <ImgDiv src={imageUrl} alt={villageName} />
            <CardInfo>
                <p>{villageName}</p>
                <div>
                    <img src={location} alt='위치표시' />
                    <p>{sidoName}</p>
                </div>
                <hr />
                <Datediv>
                    <img src={calendar} alt='캘린더' />
                    <Date>{visitDate}</Date>
                </Datediv>
                <Content>{content}</Content>
                <DeleteBtn onClick={clickDelteM}>삭제하기</DeleteBtn>
            </CardInfo>
        </Card>
    );
}

const ImgDiv = styled.img`
height:214.92px;
width:305px;
position: absolute;
top:0;
left:0;
border-radius: 10px 10px 0 0;
`

const DeleteModal = styled.div`
position: absolute;
width: 250px;
height: 130px;
background: white;
border-radius: 10px;
display:flex;
justify-content: center;
align-items:center;
flex-direction: column;
top:172.5px;
left:27.5px;
z-index:3;
border: 3px;
border-color: gray;
div{
display:flex;
width:100%;
justify-content: space-around;
margin-top: 10px
}

div>button:nth-of-type(2){
background-color: red;
}`

const DeleteBtn = styled.button`
background-color: red;
color:white;
position:absolute;
bottom: 10px;
right: 10px;
border-radius: 10px;
border:0;`

const Card = styled.div`
width: 305px;
height: 475px;
flex-shrink: 0;
border-radius: 10px;
background: #BDBDBD;
position: relative;
`
const Datediv = styled.div`
margin: 3px;
display: flex;
`

const Date = styled.p`
color: #000;
font-size: 18px;
font-style: normal;
font-weight: 400;
line-height: normal;
letter-spacing: -0.63px;
margin-bottom: 16px;
`

const Content = styled.p`
color: #000;
font-size: 12px;
font-style: normal;
font-weight: 400;
line-height: normal;
letter-spacing: -0.42px;
width: 275px;
margin:0 15px 7px 6px`

const CardInfo = styled.div`
position: absolute;
bottom:0;
width: 305px;
height: 260.082px;
flex-shrink: 0;
border-radius: 0 0 10px 10px;
background: #FFF;
padding: 13px 0 0 9px;
box-sizing: border-box;
div{
display:flex;
margin-top: 17px;

}

>p:first-of-type{
color: #000;
text-align: center;
font-size: 24px;
font-style: normal;
font-weight: 400;
line-height: normal;
letter-spacing: -0.84px;
width: 100px;
height: 24px;
flex-shrink: 0;
margin:0;
}

div>p{
text-align:left !important;
color: #000;
font-size: 18px;
font-style: normal;
font-weight: 400;
line-height: normal;
letter-spacing: -0.63px;
width: 148px;
height: 20px;
flex-shrink: 0;
margin:0;
}
div>img{
width: 22px;
height: 22px;
padding: 0 2.75px 3.92px 2.75px;
flex-shrink: 0;
aspect-ratio: 1/1;}
hr{
width: 277px;

color:#B9B9B9;
margin: 16px 0 18px 5px;
}

p{
text-align:left !important;}

`

export default MemoryCard;