import React from 'react';
import styled from 'styled-components';
import user2 from '../assets/user2.jpg'
import user3 from '../assets/user3.jpg'
import user1 from '../assets/user1.png'
import picExperience from '../assets/concept-portrait-overstimulated-person.jpg'




let citations = [
  {
    content: 'Les larmes qui coulent sont amères mais plus amères encore sont celles qui ne coulent pas',
    star: '★ ★ ★ ★ ★ ★',
    user: 'Lina Mine',
    image: user1
  },
  {
    content: 'Qui Ne Tente Rien N’a Rien',
    star: '★ ★ ★',
    user: 'Sami Loreou',
    image: user2
  },
  {
    content: 'Patience is not the ability to wait, but the ability to keep a good attitude while waiting',
    star: '★ ★ ★ ★ ★',
    user: 'Yao Perna',
    image: user3
  },
];

let experiences = [
  {
    title: 'Les cicatrices invisibles',
    content: "Je me souviens du jour où tout a changé. C'était une journée comme une autre, ou du moins, je le croyais. Je me suis réveillée, préparée pour une journée de travail, et j'ai pris mon café habituel. Tout semblait normal, mais quelque chose dans l'air était différent. Une tension que je ne pouvais pas expliquer. Une peur sourde, là, au fond de moi, que je n'avais jamais vraiment remarquée auparavant.",
    star: '★ ★ ★ ★ ★ ★',
    userName: 'Lina Mine',
    userImage: user1,
    image: picExperience,
  },
];

const CitationCard = ({ content, star, user, image }) => (
  <div className="card px-2 pb-10 pt-14 justify-center flex-col gap-y-8">
    <div className="flex flex-col gap-y-3 place-items-center justify-center min-h-[161px]">
      <p className="text-white/90 text-[18px] charm text-center">
        <span>‘’</span> {content} <span>‘’</span>
      </p>
      <div className="z-[1]">
        <StyledStars>{star}</StyledStars>
      </div>
    </div>
    <div className="z-[1] flex justify-between items-center gap-x-14">
      <p className="">
        <span>@</span>{user}
      </p>
      <img src={image} alt="user" className="h-9 rounded-full object-cover" />
    </div>
  </div>
);

const ExperienceCard = ({ title, content, star, userName, userImage, image }) => (
  <div className="card2 p-4 pl-6 flex lg:flex-row flex-col-reverse gap-y-5 gap-x-10">
    <div className='flex-4 flex flex-col lg:gap-y-14 gap-y-6'>
      <h3 className='text-white/90 lg:text-3xl text-xl charm text-center z-[1]'> <span>{title}</span> </h3>
      <p className="text-white/90 lg:text-[20px] text-[14px] charm text-center" > <span>‘’</span> {content} <span>‘’</span> </p>
      <div className='z-[1] flex justify-evenly items-center lg:flex-row flex-col gap-y-6'>
        <button className='p-2 border-gradient background-gradient'>Voir la suite</button>
        <span> {star} </span>
        <div className='flex justify-center items-center gap-x-10'>
          <p> <span>@</span>{userName} </p>
          <img src={userImage} alt="" className='h-10 rounded-full' />
        </div>
      </div>
    </div>
    <div className='z-[1] flex-1 flex justify-center '>
      <img src={image} alt="" className='lg:h-full h-[186px] rounded-lg' />
    </div>
  </div>
);

const Card = ({ isToggled }) => {
  return (
    <div className='flex justify-items-center mx-auto'>
      {/* Card 1 - visible quand switch est OFF */}
      {!isToggled && (
        <AnimatedWrapper id='card1' className='grid md:grid-cols-3 grid-row-3 md:space-x-28  space-y-6  mx-auto'>
          {citations.map((citation, index) => (
            <CitationCard key={index} {...citation} />
          ))}
        </AnimatedWrapper>
      )}

      {/* Card 2 - visible quand switch est ON */}
      {isToggled && (
        <AnimatedWrapper id='card2' className='flex mx-auto '>
          {experiences.map((experience, index) => (
            <ExperienceCard key={index} {...experience} />
          ))}
        </AnimatedWrapper>
      )}
    </div>
  );
};


const StyledStars = styled.div`
  color: #c4af39; /* Jaune doré */
  font-size: 1.2em; /* Taille de police pour les étoiles */
`;

const AnimatedWrapper = styled.div`
  opacity: 0;
  transform: translateX(-90px);
  animation: fadeInUp 2.6s ease forwards;

  @keyframes fadeInUp {
    to {
      opacity: 20;
      transform: translateX(0);
    }
  }

  .card span, .card2 span {
    color: #d1b82d;
    font-size: 1.2em;
  }

  .card {
    width: 220px;
    height: 277px;
    background: #07182E;
    position: relative;
    display: flex;
    place-content: center;
    place-items: center;
    overflow: hidden;
    border-radius: 20px;
  }

  .card2 {
    // width: 100%;
    background: #07182E;
    position: relative;
    display: flex;
    place-content: center;
    place-items: center;
    overflow: hidden;
    border-radius: 20px;
  }

  .card p, .card2 p {
    z-index: 1;
  }

  .card::before {
    content: '';
    position: absolute;
    width: 100px;
    background-image: linear-gradient(180deg, #A09F87, #4C3163);
    height: 130%;
    animation: rotBGimg 3s linear infinite;
    transition: all 0.2s linear;
  }

  .card2::before {
    content: '';
    position: absolute;
    width: 100%;
    background-image: linear-gradient(180deg, #A09F87, #4C3163);
    height: 130%;
    animation: rotBGimg 10s linear infinite;
    transition: all 0.2s linear;
  }

  .card2::after {
    content: '';
    position: absolute;
    background: #07182E;
    inset: 5px;
    border-radius: 15px;
  }

  @keyframes rotBGimg {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }

  .card::after {
    content: '';
    position: absolute;
    background: #07182E;
    inset: 5px;
    border-radius: 15px;
  }
`;

export default Card;
