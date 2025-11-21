import styled from 'styled-components';
import location from '../../assets/Location.svg';
import modalXBtn from '../../assets/ModalXBtn.svg';
import imgAddBtn from '../../assets/ImgAddBtn.svg';
import calendar from '../../assets/calendar.svg';
import saveImg from '../../assets/SaveImg.svg';
import client from '../../api/client';
import { useState } from 'react';

function MemoryAdd({ closeMModal, villageId, villageName, address, imageUrl }) {
    const [content, setContent] = useState("");
    const [visitDate, setVisitDate] = useState("");

    const clickSave = async () => {
        console.log(content);
        console.log(visitDate);
        if (!content) {

        }

        if (!villageId) {

        }

        try {
            const res = await client.post('/memories', {
                villageId: villageId,
                content: content,
                visitDate: visitDate
            });

            if (res.data.success) {
                alert("저장되었습니다.");
                closeMModal();
                window.location.reload();
            }
        } catch (error) {
            console.error("추억 저장 실패:", error);
            alert("저장에 실패했습니다. 다시 시도해주세요.");
        }
    }

    return (
        <ModalBack>
            <Modal>
                <XBtn src={modalXBtn} alt='나가기' onClick={closeMModal} />
                <ImgDiv src={imageUrl} alt={villageName} />
                <label htmlFor='imageUpload'>
                    <img src={imgAddBtn} alt='이미지추가' />
                </label>
                <VillageInfo>
                    <VillageN>{villageName}</VillageN>
                    <div>
                        <img src={location} alt='위치표시' />
                        <Address>{address}</Address>
                    </div>
                    <hr />
                    <form>
                        <input id='imageUpload' type='file' accept='image/*' />
                        <img src={calendar} alt='캘린더' />
                        <input type='date' value={visitDate} onChange={(e) => setVisitDate(e.target.value)} />
                        <textarea placeholder="내용을 입력해주세요." required value={content} onChange={(e) => setContent(e.target.value)}></textarea>
                    </form>
                    <SaveBtn onClick={clickSave}><img src={saveImg} alt='저장하기' /></SaveBtn>
                </VillageInfo>
            </Modal>
        </ModalBack >
    );
}

const XBtn = styled.img`
position: absolute;
top: 17px;
right: 13px;
z-index:1;
`

const ImgDiv = styled.img`
height:280px;
width:375px;
position: absolute;
top:0;
left:0;
border-radius: 10px 10px 0 0;
`

const ModalBack = styled.div`
width: 393px;
height: 852px;
position:absolute;
top:0;
left:0;
background: rgba(105, 104, 104, 0.60);
z-index:9998;`

const SaveBtn = styled.button`
position: absolute;
top: 271px;
left: 237px;
border:0;
padding:0;
background-color:transparent;

`

const Modal = styled.div`
width: 375px;
height: 610px;
flex-shrink: 0;
position: absolute;
top:121px;
left:9px;
background-color: #BDBDBD;
z-index:9999;
border-radius: 10px;

label{
width: 375px;
height: 280px;
display:flex;
justify-content: center;
align-items: center;
}
`
const VillageInfo = styled.div`
position: absolute;
bottom:0;
width: 375px;
height: 330px;
flex-shrink: 0;
border-radius: 0 0 10px 10px;
background: #FFF;
padding: 25px 0 0 26px;
box-sizing: border-box;
div{
display:flex;
gap:2.75px;
margin-top:17px;
}


hr{
width: 319px;

color:#B9B9B9;
margin: 20px 0 12px 0;
}
textarea{
padding:11px;
text-align:left !important;
width: 320px;
height: 92px;
flex-shrink: 0;
border-radius: 10px;
background: #BDBDBD;
border:0;
resize: none;
outline: none;
}
form>input:first-of-type{
text-align:left !important;
display:none;

}
form{
text-align:left !important;}

`

const Address = styled.p`
width:300px;
text-align:left;
color: #000;
font-size: 20px;
font-style: normal;
font-weight: 400;
line-height: normal;
letter-spacing: -0.7px;`


const VillageN = styled.p`
text-align:left;
color: #000;
font-size: 30px;
font-style: normal;
font-weight: 400;
line-height: normal;
letter-spacing: -1.05px;`

export default MemoryAdd;