import React from 'react';
import './CardContact.css';

const Form = () => {
  return (
    <div className='card-contact'>
      <div className="form-container sm:w-[400px] w-auto">
        <form className="form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input required name="email" id="email" type="text" />
          </div>
          <div className="form-group">
            <label htmlFor="textarea">Comment pouvons-nous vous aider?</label>
            <textarea required cols={50} rows={10} id="textarea" name="textarea" defaultValue={"          "} />
          </div>
          <button type="submit" className="form-submit-btn">Soumettre</button>
        </form>
      </div>
    </div>
  );
}

export default Form;
