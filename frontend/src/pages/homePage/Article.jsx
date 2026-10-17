import React, { useState } from 'react';
import magic from '../../assets/magicBook.webp';
import Switch from '../../components/Switch';
import Card from '../../components/CardArticle';

const Article = () => {
  const [isToggled, setIsToggled] = useState(false);

  return (
    <section id='Article' className='section text-white '>
      <div className='sm:container mx-auto p-[20px] pb-0 flex flex-col gap-y-4'>
        <div className='flex sm:flex-row flex-col items-center justify-between gap-y-4'>
          <div className='text-gradient lg:text-6xl text-center lg:text-left text-5xl charm'>
            Article
          </div>
          <div className='flex items-center gap-x-4'>
            <img src={magic} alt="" className='h-14 lg:h-16 aspect-square object-cover rounded-full border-3 border-gradient' />
            <Switch isToggled={isToggled} setIsToggled={setIsToggled} />
          </div>
        </div>
        <p className='text-white/80 lg:text-[20px] text-[18px] charm leading-8 lg:text-left text-center  lg:text-current '>
              Chaque expérience que vous avez vécue, chaque moment difficile ou joyeux, chaque leçon tirée d'une épreuve ou d'une réussite, a 
              le pouvoir d’inspirer et de renforcer ceux qui vous entourent. Parfois, une simple parole, un proverbe sage ou une histoire personnelle
               peut être le moteur dont quelqu'un a besoin pour aller de l'avant. Ouvrez votre cœur, partagez votre sagesse, et laissez vos expériences 
               être un phare pour ceux qui cherchent des réponses, de l’espoir, ou même un peu de réconfort. Ensemble, nous pouvons créer une communauté 
               où chaque histoire compte et chaque pensée enrichit notre voyage collectif. 🌟
        </p>

        <div className='flex'>
          <Card isToggled={isToggled} />
        </div>
      </div>
    </section>
  );
};

export default Article;
