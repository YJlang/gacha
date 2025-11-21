import styled from 'styled-components';
import collectionImg from '../../assets/trophy.svg'
import memoryImg from '../../assets/MemoryImg.svg'

function MyCollection({ content, data }) {
    return (
        <Collection>
            {content === 'collection' ? (
                <img src={collectionImg} alt='콜렉션 이미지' />
            ) : content === 'memory' ? (
                <img src={memoryImg} alt='메모리 이미지' />
            ) : (
                null
            )}
            {content === 'collection' ? (
                <div>내 컬렉션 : {data}/100</div>
            ) : content === 'memory' ? (
                <div>나의 추억 : {data}</div>
            ) : (
                null
            )}
        </Collection>
    );
}

const Collection = styled.div`

    display:flex;
    align-items: flex-end;
    color: #FFDFD6;
text-shadow: 2.094px 2.094px 0 #1E3445;
-webkit-text-stroke-width: 3.14px;
-webkit-text-stroke-color: #1E3445;

font-size: 18px;
font-style: normal;
font-weight: 400;
line-height: normal;
letter-spacing: -0.63px;
    `


export default MyCollection;