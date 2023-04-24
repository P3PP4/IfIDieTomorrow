import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';

import styled from 'styled-components';
import tw from 'twin.macro';

import diaryInactive from '../../assets/icons/diary_inactive.svg';
import diaryActive from '../../assets/icons/diary_active.svg';
import bucketInactive from '../../assets/icons/bucket_inactive.svg';
import bucketActive from '../../assets/icons/bucket_active.svg';
import homeInactive from '../../assets/icons/home_inactive.svg';
import homeActive from '../../assets/icons/home_active.svg';
import feedInactive from '../../assets/icons/feed_inactive.svg';
import feedActive from '../../assets/icons/feed_active.svg';
import mypageInactive from '../../assets/icons/mypage_inactive.svg';
import mypageActive from '../../assets/icons/mypage_active.svg';

import Button from './Button';
const Navbar = styled.div`
  ${tw`bg-gray_100`}
  width: 100%;
  display: flex;
`;
function NavBar() {
  const navigate = useNavigate();
  const [isDiary, setIsDiary] = useState<boolean>(false);
  const [isBucket, setIsBucket] = useState<boolean>(false);
  const [isHome, setIsHome] = useState<boolean>(true);
  const [isFeed, setIsFeed] = useState<boolean>(false);
  const [isMypage, setIsMypage] = useState<boolean>(false);
  const [changeNav, setChangeNav] = useState<boolean>(false);

  // useEffect(() => {
  //   if (changeNav) {
  //   }
  // }, [changeNav]);

  const handleChange = () => {
    setChangeNav((prev) => !prev);
  };

  const handleDiary = () => {
    handleChange();
    setIsDiary(true);
    setIsBucket(false);
    setIsHome(false);
    setIsFeed(false);
    setIsMypage(false);

    navigate(`/diary`);
  };

  const handleBucket = () => {
    handleChange();
    setIsDiary(false);
    setIsBucket(true);
    setIsHome(false);
    setIsFeed(false);
    setIsMypage(false);

    navigate(`/bucket`);
  };

  const handleHome = () => {
    handleChange();
    setIsDiary(false);
    setIsBucket(false);
    setIsHome(true);
    setIsFeed(false);
    setIsMypage(false);

    navigate(`/home`);
  };

  const handleFeed = () => {
    handleChange();
    setIsDiary(false);
    setIsBucket(false);
    setIsHome(false);
    setIsFeed(true);
    setIsMypage(false);

    navigate(`/feed`);
  };

  const handleMypage = () => {
    handleChange();
    setIsDiary(false);
    setIsBucket(false);
    setIsHome(false);
    setIsFeed(false);
    setIsMypage(true);

    navigate(`/mypage`);
  };

  return (
    <Navbar>
      {isDiary ? (
        <img src={diaryActive} alt="active diary button" />
      ) : (
        <img
          src={diaryInactive}
          alt="inactive diary button"
          onClick={handleDiary}
        />
      )}

      {isBucket ? (
        <img src={bucketActive} alt="active bucket button" />
      ) : (
        <img
          src={bucketInactive}
          alt="inactive bucket button"
          onClick={handleBucket}
        />
      )}

      {isHome ? (
        <img src={homeActive} alt="active home button" />
      ) : (
        <img
          src={homeInactive}
          alt="inactive home button"
          onClick={handleHome}
        />
      )}

      {isFeed ? (
        <img src={feedActive} alt="active feed button" />
      ) : (
        <img
          src={feedInactive}
          alt="inactive feed button"
          onClick={handleFeed}
        />
      )}

      {isMypage ? (
        <img src={mypageActive} alt="active mypage button" />
      ) : (
        <img
          src={mypageInactive}
          alt="inactive mypage button"
          onClick={handleMypage}
        />
      )}
    </Navbar>
  );
}

export default NavBar;