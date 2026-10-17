import React from 'react';
import './Pyramid.css';

const LoaderPyramid = () => {
  return (
    <div className='pyramid-shape'>
      <div className="pyramid-loader">
        <div className="wrapper">
          <span className="side side1" />
          <span className="side side2" />
          <span className="side side3" />
          <span className="side side4" />
          <span className="shadow" />
        </div>
      </div>
    </div>
  );
}

export default LoaderPyramid;
