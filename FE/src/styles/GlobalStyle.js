import { createGlobalStyle } from "styled-components";
import reset from "styled-reset";

const GlobalStyle = createGlobalStyle`
  ${reset}

  @font-face {
    font-family: 'NeoDonggeunmo';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2001@1.3/NeoDunggeunmo.woff') format('woff');
    font-weight: normal;
    font-style: normal;
  }

  body {
    font-family: 'NeoDonggeunmo', sans-serif;
    background-color: #f0f0f0;
  }

  #root {
    width: 393px;
    height: 852px;
    margin: 0 auto;
    position: relative;
    border: 1px solid #ccc;
    overflow: auto;
    background-color: white;

    // 스크롤 설정
    &::-webkit-scrollbar {
      display: none;
    }
    -ms-overflow-style: none;
    scrollbar-width: none;
  }

  * {
    box-sizing: border-box;
    text-align: center;
  }
`;

export default GlobalStyle;
