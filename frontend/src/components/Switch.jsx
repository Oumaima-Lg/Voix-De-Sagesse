import React from 'react';
import './Switch.css';

const Switch = ({ isToggled, setIsToggled }) => {
  return (
    <div className='switch-toggle'>
      <label className="switch-button" htmlFor="switch">
        <div className="switch-outer">
          <input
            id="switch"
            type="checkbox"
            checked={isToggled}
            onChange={() => setIsToggled(!isToggled)}
          />
          <div className="button">
            <span className="button-toggle" />
            <span className="button-indicator" />
          </div>
        </div>
      </label>
    </div>
  );
};

export default Switch;
