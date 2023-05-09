import { useEffect, useRef } from 'react';
import useOutsideClick from '../../hooks/useOutsideClick';
import styled from 'styled-components';
import tw from 'twin.macro';
import Button from '../common/Button';
import requests from '../../api/config';
import { defaultApi } from '../../api/axios';

const ModalOverlay = styled.div`
  ${tw`flex items-center justify-center z-50 bg-neutral-400/80 h-full w-full fixed`}
`;

const ModalWrapper = styled.div`
  ${tw`bg-gray-100 flex flex-col w-full mx-[16px] px-[16px] items-center border-solid rounded-xl shadow `}
`;
const ContentContainer = styled.div`
  ${tw`flex flex-wrap w-full pt-[4px] text-p2 h-[60px] bg-white rounded border-black my-[16px] px-[6px]`}
`;
interface Bucket {
  bucketId: number;
  title: string;
  complete: string;
  secret: boolean;
}
interface DeleteModalProps {
  setBuckets: React.Dispatch<React.SetStateAction<Bucket[]>>;
  selectedBucketId: number | null;
  onClose?: () => void;
}

function DeleteModal({
  selectedBucketId,
  setBuckets,
  onClose,
}: DeleteModalProps) {
  const modalRef = useRef<HTMLDivElement>(null);
  const handleDelete = () => {
    //버킷리스트 삭제 api 연결
    const get_user_bucket = async () => {
      try {
        const response = await defaultApi.get(requests.GET_USER_BUCKET(), {
          withCredentials: true,
        });
        setBuckets(response.data);
      } catch (error) {
        throw error;
      }
    };
    const delete_bucket = async () => {
      try {
        await defaultApi.delete(
          requests.DELETE_BUCKET(selectedBucketId),

          {
            withCredentials: true,
          },
        );

        get_user_bucket();
      } catch (error) {
        throw error;
      }
    };
    delete_bucket();
    onClose?.();
  };
  const handleClose = () => {
    onClose?.();
  };

  //모달 외부 클릭시 모달창 꺼짐
  useOutsideClick(modalRef, handleClose);

  //외부 스크롤 방지
  useEffect(() => {
    const $body = document.querySelector('body');
    const overflow = $body?.style.overflow;
    if ($body) {
      $body.style.overflow = 'hidden';
    }
    return () => {
      if ($body) {
        $body.style.overflow = overflow || '';
      }
    };
  }, []);

  return (
    <ModalOverlay>
      <ModalWrapper ref={modalRef}>
        <ContentContainer>
          정말 삭제하시겠습니까? 삭제된 버킷은 되돌릴 수 없습니다.
        </ContentContainer>
        <div className="flex w-full justify-center my-4">
          <Button onClick={handleDelete} color="#B3E9EB" size="sm">
            삭제하기
          </Button>
        </div>
      </ModalWrapper>
    </ModalOverlay>
  );
}

export default DeleteModal;