import React, { useState } from 'react';
import magic from '../../assets/magicBook.jpg';
import Switch from '../../semiComponents/Switch';
import Card from '../../semiComponents/CardArticle';

const Article = () => {
  const [isToggled, setIsToggled] = useState(false);

  return (
    <section id='Article' className='section text-white '>
      <div className='sm:container mx-auto p-[20px] pt-22 pb-0 flex flex-col lg:gap-y-15 gap-y-12'>
        <div className='text-gradient lg:text-6xl text-center lg:text-left text-5xl charm'>
          Article
        </div>
        <div className='flex lg:flex-row flex-col gap-y-10 items-center'>
          <div className='flex-2'>
            <p className='text-white/80 lg:text-[25px] text-[18px] charm lg:leading-10 leading-8 lg:text-left text-center  lg:text-current '>
              Chaque expérience que vous avez vécue, chaque moment difficile ou joyeux, chaque leçon tirée d'une épreuve ou d'une réussite, a le pouvoir d’inspirer et de renforcer ceux qui vous entourent. Parfois, une simple parole, un proverbe sage ou une histoire personnelle peut être le moteur dont quelqu'un a besoin pour aller de l'avant. Ouvrez votre cœur, partagez votre sagesse, et laissez vos expériences être un phare pour ceux qui cherchent des réponses, de l’espoir, ou même un peu de réconfort. Ensemble, nous pouvons créer une communauté où chaque histoire compte et chaque pensée enrichit notre voyage collectif. 🌟
            </p>
          </div>
          <div className='flex-1 flex flex-col justify-center items-center gap-y-3'>
            <img src={magic} alt="" className='h-auto max-h-50 rounded-full border-3 border-gradient' />
            <div>
              <Switch isToggled={isToggled} setIsToggled={setIsToggled} />
            </div>
          </div>
        </div>

        <div className='flex'>
          <Card isToggled={isToggled} />
        </div>
      </div>
    </section>
  );
};

export default Article;
