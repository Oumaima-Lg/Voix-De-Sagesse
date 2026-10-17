import React, { useEffect, useState } from 'react'
import { Link as LinkRouter } from "react-router-dom";
import { useSelector } from 'react-redux';

const SECTIONS = [
  { id: 'Accueil', label: 'Accueil' },
  { id: 'About', label: 'A propos' },
  { id: 'Article', label: 'Article' },
  { id: 'Contact', label: 'Contact' },
];

// Height of the fixed header, so a section title is not hidden behind it.
const HEADER_OFFSET = 90;

const sectionTop = (id) => {
  const el = document.getElementById(id);
  return el ? el.getBoundingClientRect().top + window.scrollY : null;
};

const scrollToSection = (id) => {
  const top = sectionTop(id);
  if (top !== null) {
    window.scrollTo({ top: top - HEADER_OFFSET, behavior: 'smooth' });
  }
};

// The active section is the last one whose top has reached the upper third of
// the screen. At the very bottom of the page the last section is active, even
// if it is too short to reach that point.
const useActiveSection = () => {
  const [active, setActive] = useState(SECTIONS[0].id);

  useEffect(() => {
    const update = () => {
      const atBottom = window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 2;
      if (atBottom) {
        setActive(SECTIONS[SECTIONS.length - 1].id);
        return;
      }
      const probe = window.scrollY + window.innerHeight / 3;
      let current = SECTIONS[0].id;
      for (const { id } of SECTIONS) {
        const top = sectionTop(id);
        if (top !== null && top <= probe) current = id;
      }
      setActive(current);
    };

    update();
    window.addEventListener('scroll', update, { passive: true });
    window.addEventListener('resize', update);
    return () => {
      window.removeEventListener('scroll', update);
      window.removeEventListener('resize', update);
    };
  }, []);

  return active;
};

const HeaderNav = ({ isHidden }) => {
  const user = useSelector((state) => state.user);
  const active = useActiveSection();

  return (
    <>
      {SECTIONS.map(({ id, label }) => (
        <a
          key={id}
          href={`#${id}`}
          onClick={(e) => { e.preventDefault(); scrollToSection(id); }}
          className={`${!isHidden ? "block" : "hidden"} text-white cursor-pointer ${active === id ? 'font-bold border-b-2 border-[var(--color-start)]' : ''}`}
        >
          {label}
        </a>
      ))}
      {user ? <LinkRouter to='/articla' className={`${!isHidden ? "block" : "hidden"} text-white cursor-pointer border-2! border-gradient hover:border-white/45 background-gradient p-2 `}>
                Mon espace
              </LinkRouter>
            : <LinkRouter to='/auth/login' className={`${!isHidden ? "block" : "hidden"} text-white cursor-pointer border-2! border-gradient hover:border-white/45 background-gradient p-2 `}>
                Se connecter
              </LinkRouter>
      }
      <a href="/auth/login" className={`${!isHidden ? "hidden" : "block"} text-white cursor-pointer border-2! border-gradient hover:border-white/45 background-gradient p-2 `}>
        Se connecter
      </a>
    </>
  )
}

export default HeaderNav
