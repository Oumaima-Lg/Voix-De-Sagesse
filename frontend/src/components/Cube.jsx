import React from 'react';
import styled from 'styled-components';

const Cube = () => {
  return (
    <StyledWrapper>
      <div className="cube">
        <div className="cube__face" id="cube__face--front">
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
        </div>
        <div className="cube__face" id="cube__face--back">
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
        </div>
        <div className="cube__face" id="cube__face--right">
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
        </div>
        <div className="cube__face" id="cube__face--left">
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
        </div>
        <div className="cube__face" id="cube__face--top">
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
        </div>
        <div className="cube__face" id="cube__face--bottom">
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
          <span className="faceBox" />
        </div>
      </div>
    </StyledWrapper>
  );
}

const StyledWrapper = styled.div`
  .cube {
    width: 100px;
    height: 100px;
    transform-style: preserve-3d;
    animation: animate 4s linear infinite;
    cursor: pointer;
    transition: 0.5s;
  }

  .cube:hover {
    scale: 1.2;
    animation: animate 3s linear infinite;
  }

  .cube__face {
    position: absolute;
    width: 100px;
    height: 100px;
    display: flex;
    align-items: center;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .faceBox {
    border: calc(100px / 1000) solid rgba(0, 0, 0, 0.8);
    width: calc(100px / 3);
    height: calc(100px / 3);
    opacity: 1;
  }

  #cube__face--front span {
    background-color: #FF4949;
  }

  #cube__face--right span {
    background-color: #13CE66;
  }

  #cube__face--left span {
    background-color: #2D8EFF;
  }

  #cube__face--top span {
    background-color: #FFCC3D;
  }

  #cube__face--bottom span {
    background-color: #fefefe;
  }

  #cube__face--back span {
    background-color: #f1f03e;
  }

  #cube__face--front {
    transform: rotateY(0deg) translateZ(calc(100px / 2));
  }

  #cube__face--right {
    transform: rotateY(90deg) translateZ(calc(100px / 2));
  }

  #cube__face--back {
    transform: rotateY(180deg) translateZ(calc(100px / 2));
  }

  #cube__face--left {
    transform: rotateY(-90deg) translateZ(calc(100px / 2));
  }

  #cube__face--top {
    transform: rotateX(90deg) translateZ(calc(100px / 2));
  }

  #cube__face--bottom {
    transform: rotateX(-90deg) translateZ(calc(100px / 2));
  }

  @keyframes animate {
    0% {
      transform: rotateX(-30deg) rotateY(0deg) rotateZ(0deg);
    }

    100% {
      transform: rotateX(-30deg) rotateY(360deg) rotateZ(360deg);
    }
  }`;

export default Cube;
