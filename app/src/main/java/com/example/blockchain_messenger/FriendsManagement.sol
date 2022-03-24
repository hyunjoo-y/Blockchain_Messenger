pragma solidity >=0.4.22 <0.9.0;

contract AddMemberInfo {

    struct memberInfo{
        uint addrIndex;
        uint nickIndex;
        string libp2pAddress;
        address UserAddress;
        string password;
        string IPFShash;
    }

    struct memberFriends{
        string[] friendsList;
    }


    mapping(string => memberInfo) internal map;
    mapping(string => memberFriends) internal frList;
    address[] internal addrList;
    string[] internal nicknameList;

    // 닉네임 - libp2p, 이더리움 주소, IPFS
    function set(string memory _key, string memory _libp2pAddr, address _userAddr, string memory _pass, string memory _ipfs) public{
        memberInfo storage Info = map[_key];
        uint index = Info.addrIndex;

        // 이미 존재하는 닉네임이라면
        if(index > 0){
            // 주소가 다르다면 저장 안 함
            if(_userAddr != Info.UserAddress)
                return;
            // do nothing
            else {
                // 덮어쓰기
                Info.libp2pAddress = _libp2pAddr;
                Info.UserAddress = _userAddr;
                Info.password = _pass;
                Info.IPFShash = _ipfs;

                return;
            }
        }
        // 처음 등록하는 경우
        else{
            // 회원 목록
            addrList.push(_userAddr);
            // 닉네임 리스트(중복 처리)
            nicknameList.push(_key);
            // 저장
            Info.libp2pAddress = _libp2pAddr;
            Info.UserAddress = _userAddr;
            Info.password = _pass;
            Info.IPFShash = _ipfs;

            uint addrListIndex = addrList.length;
            uint nickListIndex = nicknameList.length;
            Info.addrIndex = addrListIndex;
            Info.nickIndex = nickListIndex;
        }
    }

    // 친구 추가
    function setFriend(string memory _key, string memory _friendName) public {
        memberFriends storage Info = frList[_key];

        // 이미 친구가 있다면
        if(Info.friendsList.length > 0){
            // 친구 중복인지
            for(uint i = 0; i < Info.friendsList.length - 1; i++){
                // 중복이면 종료
                if(keccak256(bytes(Info.friendsList[i])) == keccak256(bytes(_friendName)))
                    return;
            }
            // 아니면 추가
            Info.friendsList.push(_friendName);
        }
        // 첫 친구 추가라면
        else{
            Info.friendsList.push(_friendName);
        }
    }


    function setLibp2pAddr(string memory _key, string memory _libp2pAddr) public {
        memberInfo storage Info = map[_key];

        Info.libp2pAddress = _libp2pAddr;
    }

    function setPassword(string memory _key, string memory _pass) public {
        memberInfo storage Info = map[_key];

        Info.password = _pass;
    }

    function setIPFSHash(string memory _key, string memory _ipfs) public {
        memberInfo storage Info = map[_key];

        Info.IPFShash = _ipfs;
    }

    function deleteFriend(string memory _key, string memory _frName) public{
        memberFriends storage Info = frList[_key];

        require(Info.friendsList.length != 0);

        // 마지막 인덱스
        uint LastIndex = Info.friendsList.length - 1;

        // 삭제할 친구이름 인덱스
        uint deleteIndex = 0;
        for(uint i = 0; i < LastIndex; i++){
            if(keccak256(bytes(Info.friendsList[i])) == keccak256(bytes(_frName))){
                deleteIndex = i;
                break;
            }
        }

        string memory temp = Info.friendsList[LastIndex];
        // 마지막 인덱스에 삭제할 친구 넣어줌
        Info.friendsList[LastIndex] = Info.friendsList[deleteIndex];
        // 마지막 인덱스에 있던 친구 옮겨줌
        Info.friendsList[deleteIndex] = temp;

        // 친구 삭제
        Info.friendsList.pop();
    }

    // 탈퇴 시 삭제
    function remove(string memory _key) public {
        memberInfo storage Info = map[_key];

        require(Info.addrIndex != 0); // 정보가 존재해야 하고
        require(Info.addrIndex <= addrList.length); // 올바른 인덱스를 가지고 있어야한다.

        // 삭제할 회원의 인덱스
        uint addrListIndex = Info.addrIndex - 1;
        // 마지막 회원의 인덱스
        uint addrListLastIndex = addrList.length - 1;
        // 삭제할 회원 닉네임 인덱스
        uint nickListIndex = Info.nickIndex - 1;
        // 마지막 회원 닉네임 인덱스
        uint nickListLastIndex = nicknameList.length - 1;
        // 삭제할 회원의 친구 목록


        // 회원 정보 삭제
        address temp = addrList[addrListLastIndex];
        addrList[addrListLastIndex] = addrList[addrListIndex];
        addrList[addrListIndex] = temp;

        addrList.pop();

        // 닉네임 삭제
        string memory temp2 = nicknameList[nickListLastIndex];
        nicknameList[nickListLastIndex] = nicknameList[nickListIndex];
        nicknameList[nickListIndex] = temp2;
        nicknameList.pop();

        // 바뀐 인덱스로 저장
        memberInfo storage Info2 = map[temp2];
        Info2.addrIndex = addrListIndex;
        Info2.nickIndex = nickListIndex;

        delete map[_key]; // 삭제(탈퇴)
        delete frList[_key];
    }

    // 회원 수
    function size() public view returns(uint) {
        return uint(addrList.length);
    }

    // 회원 존재 여부
    function contains(string memory _key) public view returns(bool){
        return map[_key].addrIndex > 0;
    }

    // 친구 닉네임 통해서 libp2p 주소 가져오기
    function getlibp2pAddress(string memory _key) public view returns(string memory){
        return map[_key].libp2pAddress;
    }

    // 친구 닉네임 통해서 이더리움 주소 가져오기
    function getEthAddress(string memory _key) public view returns(address){
        return map[_key].UserAddress;
    }
    // 친구 닉네임 통해서 이더리움 주소 가져오기
    function getPassword(string memory _key) public view returns(string memory){
        return map[_key].password;
    }
    // 친구 닉네임 통해서 IPFS 가져오기
    function getIPFSHash(string memory _key) public view returns(string memory){
        return map[_key].IPFShash;
    }
}