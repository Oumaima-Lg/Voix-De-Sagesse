import React from 'react';
import styled from 'styled-components';
import user2 from '../assets/user2.jpg'
import user3 from '../assets/user3.jpg'
import user1 from '../assets/user1.png'
import picExperience from '../assets/concept-portrait-overstimulated-person.jpg'
import picExperience2 from '../assets/un-enfant-adorable-jouant-avec-des-ombres.jpg'
import picExperience3 from '../assets/peinture-d-une-personne-souffrant-d-anxiete.jpg'
import picExperience4 from '../assets/formes-geometriques-3d-en-arriere-plan-naturel.jpg'
import picExperience5 from '../assets/rendu-d-une-personne-avec-des-personnalites-differentes.jpg'
import Tilt from 'react-parallax-tilt';
import { Carousel } from '@mantine/carousel';
// import classes from '../semiComponents/module.css'

let citations = [
  {
    content: 'Les larmes qui coulent sont amères mais plus amères encore sont celles qui ne coulent pas',
    star: '★ ★ ★ ★ ★ ★',
    emptyStar: '',
    user: 'Lina Mine',
    image: user1
  },
  {
    content: 'Qui Ne Tente Rien N’a Rien',
    star: '★ ★ ★',
    emptyStar: ' ★ ★ ★',
    user: 'Sami Loreou',
    image: user2
  },
  {
    content: 'Patience is not the ability to wait, but the ability to keep a good attitude while waiting',
    star: '★ ★ ★ ★ ★',
    emptyStar: ' ★',
    user: 'Yao Perna',
    image: user3
  },
];


let citations2 = [
  {
    content: 'Les larmes qui coulent sont amères mais plus amères encore sont celles qui ne coulent pas',
    star: '★ ★ ★ ★ ★ ★',
    emptyStar: '',
    user: 'Lina Mine',
    image: user1
  },
  {
    content: 'Qui Ne Tente Rien N’a Rien',
    star: '★ ★ ★',
    emptyStar: ' ★ ★ ★',
    user: 'Sami Loreou',
    image: user2
  },
  {
    content: 'Patience is not the ability to wait, but the ability to keep a good attitude while waiting',
    star: '★ ★ ★ ★ ★',
    emptyStar: ' ★',
    user: 'Yao Perna',
    image: user3
  },
  {
    content: 'Les larmes qui coulent sont amères mais plus amères encore sont celles qui ne coulent pas',
    star: '★ ★ ★ ★ ★ ★',
    emptyStar: '',
    user: 'Lina Mine',
    image: user1
  },
  {
    content: 'Qui Ne Tente Rien N’a Rien',
    star: '★ ★ ★',
    emptyStar: ' ★ ★ ★',
    user: 'Sami Loreou',
    image: user2
  },
  {
    content: 'Patience is not the ability to wait, but the ability to keep a good attitude while waiting',
    star: '★ ★ ★ ★ ★',
    emptyStar: ' ★',
    user: 'Yao Perna',
    image: user3
  },
  {
    content: 'Les larmes qui coulent sont amères mais plus amères encore sont celles qui ne coulent pas',
    star: '★ ★ ★ ★ ★ ★',
    emptyStar: '',
    user: 'Lina Mine',
    image: user1
  },
  {
    content: 'Qui Ne Tente Rien N’a Rien',
    star: '★ ★ ★',
    emptyStar: ' ★ ★ ★',
    user: 'Sami Loreou',
    image: user2
  },
  {
    content: 'Patience is not the ability to wait, but the ability to keep a good attitude while waiting',
    star: '★ ★ ★ ★ ★',
    emptyStar: ' ★',
    user: 'Yao Perna',
    image: user3
  },
  {
    content: 'Les larmes qui coulent sont amères mais plus amères encore sont celles qui ne coulent pas',
    star: '★ ★ ★ ★ ★ ★',
    emptyStar: '',
    user: 'Lina Mine',
    image: user1
  },
  {
    content: 'Qui Ne Tente Rien N’a Rien',
    star: '★ ★ ★',
    emptyStar: ' ★ ★ ★',
    user: 'Sami Loreou',
    image: user2
  },
  {
    content: 'Patience is not the ability to wait, but the ability to keep a good attitude while waiting',
    star: '★ ★ ★ ★ ★',
    emptyStar: ' ★',
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

let experiences2 = [
  {
    title: 'Les cicatrices invisibles',
    content: "Je me souviens du jour où tout a changé. C'était une journée comme une autre, ou du moins, je le croyais. Je me suis réveillée, préparée pour une journée de travail, et j'ai pris mon café habituel. Tout semblait normal, mais quelque chose dans l'air était différent. Une tension que je ne pouvais pas expliquer. Une peur sourde, là, au fond de moi, que je n'avais jamais vraiment remarquée auparavant.",
    star: '★ ★ ★ ★ ★',
    userName: 'Lina Mine',
    userImage: user1,
    image: picExperience,
  },
  {
    title: 'Un pas vers la lumière',
    content: "Après des années de doutes et de luttes intérieures, j’ai enfin trouvé la force de demander de l’aide. Ce n’était pas facile, mais c’était nécessaire. Aujourd’hui, je vois chaque petit progrès comme une victoire. Chaque sourire retrouvé est une preuve que l’espoir existe, même dans l’ombre.",
    star: '★ ★ ★ ★ ☆',
    userName: 'Yassine B.',
    userImage: user2,
    image: picExperience2,
  },
  {
    title: 'Le silence brisé',
    content: "J’ai longtemps gardé mes douleurs pour moi, pensant que personne ne comprendrait. Mais le jour où j’ai décidé d’en parler, j’ai senti un poids énorme disparaître. Ce n’était pas la fin de mon chemin, mais c’était le début d’une guérison.",
    star: '★ ★ ★ ★ ★',
    userName: 'Amira K.',
    userImage: user3,
    image: picExperience3,
  },
  {
    title: 'Le courage de continuer',
    content: "Il y a eu des jours où je voulais tout abandonner. Pourtant, quelque chose en moi m’a poussé à avancer, même quand tout semblait perdu. Aujourd’hui, je sais que ma force vient de ces moments où j’ai refusé de céder au désespoir.",
    star: '★ ★ ★ ☆ ☆',
    userName: 'Karim L.',
    userImage: user2,
    image: picExperience4,
  },
  {
    title: 'Un nouveau départ',
    content: "J’ai compris que le passé ne définit pas qui je suis. Ce qui compte, ce sont les choix que je fais aujourd’hui. Et aujourd’hui, je choisis d’avancer, de pardonner, et de me reconstruire pas à pas.",
    star: '★ ★ ★ ★ ★',
    userName: 'Sofia R.',
    userImage: user1,
    image: picExperience5,
  }
];

// let experiences2 = [
//   {
//     title: 'Les cicatrices invisibles',
//     content: "Je me souviens du jour où tout a changé. C'était une journée comme une autre, ou du moins, je le croyais. Je me suis réveillée, préparée pour une journée de travail, et j'ai pris mon café habituel. Tout semblait normal, mais quelque chose dans l'air était différent. Une tension que je ne pouvais pas expliquer. Une peur sourde, là, au fond de moi, que je n'avais jamais vraiment remarquée auparavant.",
//     star: '★ ★ ★ ★ ★ ★',
//     userName: 'Lina Mine',
//     userImage: user1,
//     image: picExperience,
//   },
//   {
//     title: 'Les cicatrices invisibles',
//     content: "Je me souviens du jour où tout a changé. C'était une journée comme une autre, ou du moins, je le croyais. Je me suis réveillée, préparée pour une journée de travail, et j'ai pris mon café habituel. Tout semblait normal, mais quelque chose dans l'air était différent. Une tension que je ne pouvais pas expliquer. Une peur sourde, là, au fond de moi, que je n'avais jamais vraiment remarquée auparavant.",
//     star: '★ ★ ★ ★ ★ ★',
//     userName: 'Lina Mine',
//     userImage: user1,
//     image: picExperience,
//   },
//   {
//     title: 'Les cicatrices invisibles',
//     content: "Je me souviens du jour où tout a changé. C'était une journée comme une autre, ou du moins, je le croyais. Je me suis réveillée, préparée pour une journée de travail, et j'ai pris mon café habituel. Tout semblait normal, mais quelque chose dans l'air était différent. Une tension que je ne pouvais pas expliquer. Une peur sourde, là, au fond de moi, que je n'avais jamais vraiment remarquée auparavant.",
//     star: '★ ★ ★ ★ ★ ★',
//     userName: 'Lina Mine',
//     userImage: user1,
//     image: picExperience,
//   },
// ];

const CitationCard = ({ content, star, emptyStar, user, image }) => (
  <Tilt className="card px-2 pb-10 pt-14 justify-center flex-col gap-y-8">
    <div className="flex flex-col gap-y-3 place-items-center justify-center min-h-[161px]">
      <p className="text-white/90 text-[18px] charm text-center">
        <span>‘’</span> {content} <span>‘’</span>
      </p>
      <div className="z-[1] flex items-center gap-1">
        <StyledStars>{star}</StyledStars>
        <p className='text-[1.2em] text-gray-400'>{emptyStar}</p>
      </div>
    </div>
    <div className="z-[1] flex justify-between items-center gap-x-14">
      <p className="">
        <span>@</span>{user}
      </p>
      <img src={image} alt="user" className="h-9 rounded-full object-cover" />
    </div>
  </Tilt>
);



const CitationCard2 = ({ content, star, emptyStar, user, image }) => (
  <Carousel.Slide >
    <AnimatedWrapper2 >
      <div className="card my-4 mx-2 px-2 pb-10 pt-14 justify-center flex-col gap-y-8  hover: cursor-pointer hover:shadow-[0_0_5px_2px_black] shadow-amber-50">
        <div className="flex flex-col gap-y-3 place-items-center justify-center min-h-[161px]">
          <p className="text-white/90 text-[18px] charm text-center">
            <span>‘’</span> {content} <span>‘’</span>
          </p>
          <div className="z-[1] flex items-center gap-1">
            <StyledStars>{star}</StyledStars>
            <p className='text-[1.2em] text-gray-400'>{emptyStar}</p>
          </div>
        </div>
        <div className="z-[1] flex justify-between items-center gap-x-14">
          <p className="">
            <span>@</span>{user}
          </p>
          <img src={image} alt="user" className="h-9 rounded-full object-cover" />
        </div>
      </div>
    </AnimatedWrapper2>
  </Carousel.Slide>
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


const ExperienceCard2 = ({ title, content, star, userName, userImage, image }) => (
  <Carousel.Slide >
    <AnimatedWrapper2 id='card2' className=''>
      {/* flex mx-auto  */}
      <div className="card2 h-96 my-4 mx-2  p-4 pl-6 flex lg:flex-row flex-col-reverse gap-y-5 gap-x-10  hover:shadow-[0_0_5px_2px_black] shadow-amber-50">
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
    </AnimatedWrapper2>
  </Carousel.Slide>
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



const Card2 = ({ isToggled }) => {
  return (

    <div className=' p-10'>
      {/* Card 1 - visible quand switch est OFF */}
      {!isToggled && (
        <Carousel slideSize="30%"  height={300} slideGap="md" controlsOffset="xs" controlSize={40} emblaOptions={{
          loop: false,
          dragFree: true,
          align: 'center',

        }}
          classNames={{ control: 'carousel-control' }}
        >


          {citations2.map((citation, index) => (
            <CitationCard2 key={index} {...citation} />
          ))}

        </Carousel>
      )}

      {/* Card 2 - visible quand switch est ON */}
      {isToggled && (
        <Carousel  slideGap="md" controlsOffset="xs" controlSize={40} emblaOptions={{
          loop: false,
          dragFree: true,
          align: 'center',

        }}
          classNames={{ control: 'carousel-control' }}
        >

          {experiences2.map((experience, index) => (
            <ExperienceCard2 key={index} {...experience} />
          ))}

        </Carousel>
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
  transform: translateX(-100px);
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


const AnimatedWrapper2 = styled.div`
  

  .card span, .card2 span {
    color: #d1b82d;
    font-size: 1.2em;
  }

  .card {
    width: 220px;
    height: 277px;
    background: #16191f;
    position: relative;
    display: flex;
    place-content: center;
    place-items: center;
    overflow: hidden;
    border-radius: 20px;
  }

  .card2 {
    // width: 100%;
    background: #16191f;
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

  


`;

export default Card;
export { Card2 };