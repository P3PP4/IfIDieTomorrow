import React from 'react';
import { useState, useEffect } from 'react';
import { Route, Routes, BrowserRouter } from 'react-router-dom';

import Home from './pages/Home/Home';
import Room from './pages/Home/Room';
import Bucket from './pages/Bucket/Bucket';
import Diary from './pages/Diary/Diary';
import Login from './pages/LogIn/Login';
import Nickname from './pages/LogIn/Nickname';
import ServiceAgreement from './pages/Profile/ServiceAgreement';
import FeedList from './pages/Feed/FeedList';
import DiaryDetail from './pages/Diary/DiaryDetail';
import BucketDetail from './pages/Bucket/BucketDetail';
import MyPage from './pages/Profile/MyPage';
import Survey from './pages/Diary/Survey';
import PhotoCloud from './pages/PhotoCloud/PhotoCloud';
import CreateCategory from './pages/PhotoCloud/CreateCategory';
import UploadPhoto from './pages/PhotoCloud/UploadPhoto';

function App() {
  return (
    <BrowserRouter className="App">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/home" element={<Home />} />
        <Route path="/room" element={<Room />} />
        <Route path="/bucket" element={<Bucket />} />
        <Route path="/diary" element={<Diary />} />
        <Route path="/login" element={<Login />} />
        <Route path="/nickname" element={<Nickname />} />
        <Route path="/feed" element={<FeedList />} />
        <Route path="/service-agreement" element={<ServiceAgreement />} />
        <Route path="/diary/:diaryId" element={<DiaryDetail />} />
        <Route path="/bucket/:bucketId" element={<BucketDetail />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/survey" element={<Survey />} />
        <Route path="/photo-cloud" element={<PhotoCloud />} />
        <Route
          path="/photo-cloud/create-category"
          element={<CreateCategory />}
        />
        <Route
          path="/photo-cloud/:category/upload-photo"
          element={<UploadPhoto />}
        />
        {/* <Route path="/*" element={<ErrorPage />} /> */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;