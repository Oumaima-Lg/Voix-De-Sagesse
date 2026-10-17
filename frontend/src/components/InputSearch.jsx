import React from 'react';
import './InputSearch.css';

const Input = () => {
  return (
    <div className='input-search'>
      <div>
        <div className="" />
        <div id="search-container">
          <div className="nebula" />
          <div className="starfield" />
          <div className="cosmic-dust" />
          <div className="cosmic-dust" />
          <div className="cosmic-dust" />
          <div className="stardust" />
          <div className="cosmic-ring" />
          <div id="main">
            <input className="input" name="text" type="text" placeholder="Rechercher un article..." />
            <div id="input-mask" />
            <div id="cosmic-glow" />
            <div className="wormhole-border" />
            <div id="wormhole-icon" className='cursor-pointer'>
              <svg strokeLinejoin="round" strokeLinecap="round" strokeWidth={2} stroke="#A09F87" fill="none" height={24} width={24} viewBox="0 0 24 24">
                <circle r={10} cy={12} cx={12} />
                <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
                <path d="M2 12h20" />
              </svg>
            </div>
            <div id="search-icon" className='cursor-pointer'>
              <svg strokeLinejoin="round" strokeLinecap="round" strokeWidth={2} stroke="#A09F87" fill="none" height={24} width={24} viewBox="0 0 24 24">
                <circle r={8} cy={11} cx={11} />
                <line y2="16.65" x2="16.65" y1={21} x1={21} />
                <defs>
                  <linearGradient gradientTransform="rotate(45)" id="cosmic-search">
                    <stop stopColor="#a9c7ff" offset="0%" />
                    <stop stopColor="#6e8cff" offset="100%" />
                  </linearGradient>
                </defs>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Input;
