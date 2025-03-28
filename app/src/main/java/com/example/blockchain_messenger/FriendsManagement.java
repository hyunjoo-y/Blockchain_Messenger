package com.example.blockchain_messenger;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class FriendsManagement extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50611236806100206000396000f3fe608060405234801561001057600080fd5b50600436106100cf5760003560e01c80636833d54f1161008c57806390f939e11161006657806390f939e1146101a7578063949d225d146101ba5780639d2d225e146101cb578063ca386496146101f657600080fd5b80636833d54f1461015e5780636fdf64151461018157806380599e4b1461019457600080fd5b8063019269e2146100d457806301b8c31c146100e95780630a9745c5146101125780634a5f66c114610125578063574d65541461013857806366c0c54f1461014b575b600080fd5b6100e76100e2366004610ecb565b610209565b005b6100fc6100f7366004610f2f565b61041f565b6040516101099190610f98565b60405180910390f35b6100e7610120366004610fcb565b6104d2565b6100fc610133366004610f2f565b61067f565b6100e7610146366004610ecb565b6106ad565b6100fc610159366004610f2f565b6106ec565b61017161016c366004610f2f565b61071a565b6040519015158152602001610109565b6100e761018f366004610ecb565b610744565b6100e76101a2366004610f2f565b61082c565b6100e76101b5366004610ecb565b610bf7565b600254604051908152602001610109565b6101de6101d9366004610f2f565b610c30565b6040516001600160a01b039091168152602001610109565b6100e7610204366004610ecb565b610c63565b600060018360405161021b9190611099565b908152604051908190036020019020805490915061023857600080fd5b8054600090610249906001906110cb565b90506000805b828110156102b157845160208601208454859083908110610272576102726110e2565b906000526020600020016040516102899190611133565b6040518091039020141561029f578091506102b1565b806102a9816111cf565b91505061024f565b5060008360000183815481106102c9576102c96110e2565b9060005260206000200180546102de906110f8565b80601f016020809104026020016040519081016040528092919081815260200182805461030a906110f8565b80156103575780601f1061032c57610100808354040283529160200191610357565b820191906000526020600020905b81548152906001019060200180831161033a57829003601f168201915b50505050509050836000018281548110610373576103736110e2565b90600052602060002001846000018481548110610392576103926110e2565b906000526020600020019080546103a8906110f8565b6103b3929190610c9c565b50808460000183815481106103ca576103ca6110e2565b9060005260206000200190805190602001906103e7929190610d27565b5083548490806103f9576103f96111ea565b6001900381819060005260206000200160006104159190610d9b565b9055505050505050565b60606000826040516104319190611099565b9081526020016040518091039020600401805461044d906110f8565b80601f0160208091040260200160405190810160405280929190818152602001828054610479906110f8565b80156104c65780601f1061049b576101008083540402835291602001916104c6565b820191906000526020600020905b8154815290600101906020018083116104a957829003601f168201915b50505050509050919050565b600080866040516104e39190611099565b908152604051908190036020019020805490915080156105815760038201546001600160a01b0386811691161461051b575050610678565b85516105309060028401906020890190610d27565b506003820180546001600160a01b0319166001600160a01b03871617905583516105639060048401906020870190610d27565b5082516105799060058401906020860190610d27565b505050610678565b6002805460018082019092557f405787fa12a823e0f2b7631cc41b3ba8828b3321ca811111fa75cd3aa3bb5ace0180546001600160a01b0319166001600160a01b0388161790556003805491820181556000528751610607917fc2575a0e9e593c00f959f8c92f12db2869c3395a3b0502d05e2516446f71f85b019060208a0190610d27565b50855161061d9060028401906020890190610d27565b506003820180546001600160a01b0319166001600160a01b03871617905583516106509060048401906020870190610d27565b5082516106669060058401906020860190610d27565b50600254600354908355600183015550505b5050505050565b60606000826040516106919190611099565b9081526020016040518091039020600501805461044d906110f8565b600080836040516106be9190611099565b90815260200160405180910390209050818160050190805190602001906106e6929190610d27565b50505050565b60606000826040516106fe9190611099565b9081526020016040518091039020600201805461044d906110f8565b60008060008360405161072d9190611099565b908152604051908190036020019020541192915050565b60006001836040516107569190611099565b9081526040519081900360200190208054909150156108065760005b8154610780906001906110cb565b8110156107df578251602084012082548390839081106107a2576107a26110e2565b906000526020600020016040516107b99190611133565b604051809103902014156107cd5750505050565b806107d7816111cf565b915050610772565b508054600181018255600082815260209081902084516106e6939190910191850190610d27565b8054600181018255600082815260209081902084516106e6939190910191850190610d27565b6000808260405161083d9190611099565b908152604051908190036020019020805490915061085a57600080fd5b6002548154111561086a57600080fd5b805460009061087b906001906110cb565b600254909150600090610890906001906110cb565b90506000600184600101546108a591906110cb565b6003549091506000906108ba906001906110cb565b90506000600284815481106108d1576108d16110e2565b600091825260209091200154600280546001600160a01b03909216925090869081106108ff576108ff6110e2565b600091825260209091200154600280546001600160a01b03909216918690811061092b5761092b6110e2565b9060005260206000200160006101000a8154816001600160a01b0302191690836001600160a01b03160217905550806002868154811061096d5761096d6110e2565b9060005260206000200160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555060028054806109ac576109ac6111ea565b600082815260208120820160001990810180546001600160a01b031916905590910190915560038054849081106109e5576109e56110e2565b9060005260206000200180546109fa906110f8565b80601f0160208091040260200160405190810160405280929190818152602001828054610a26906110f8565b8015610a735780601f10610a4857610100808354040283529160200191610a73565b820191906000526020600020905b815481529060010190602001808311610a5657829003601f168201915b5050505050905060038481548110610a8d57610a8d6110e2565b9060005260206000200160038481548110610aaa57610aaa6110e2565b90600052602060002001908054610ac0906110f8565b610acb929190610c9c565b508060038581548110610ae057610ae06110e2565b906000526020600020019080519060200190610afd929190610d27565b506003805480610b0f57610b0f6111ea565b600190038181906000526020600020016000610b2b9190610d9b565b905560008082604051610b3e9190611099565b908152604051908190036020018120888155600181018790559150600090610b67908b90611099565b908152604051908190036020019020600080825560018201819055610b8f6002830182610d9b565b6003820180546001600160a01b0319169055610baf600483016000610d9b565b610bbd600583016000610d9b565b5050600189604051610bcf9190611099565b9081526040519081900360200190206000610bea8282610dd8565b5050505050505050505050565b60008083604051610c089190611099565b90815260200160405180910390209050818160020190805190602001906106e6929190610d27565b60008082604051610c419190611099565b908152604051908190036020019020600301546001600160a01b031692915050565b60008083604051610c749190611099565b90815260200160405180910390209050818160040190805190602001906106e6929190610d27565b828054610ca8906110f8565b90600052602060002090601f016020900481019282610cca5760008555610d17565b82601f10610cdb5780548555610d17565b82800160010185558215610d1757600052602060002091601f016020900482015b82811115610d17578254825591600101919060010190610cfc565b50610d23929150610df6565b5090565b828054610d33906110f8565b90600052602060002090601f016020900481019282610d555760008555610d17565b82601f10610d6e57805160ff1916838001178555610d17565b82800160010185558215610d17579182015b82811115610d17578251825591602001919060010190610d80565b508054610da7906110f8565b6000825580601f10610db7575050565b601f016020900490600052602060002090810190610dd59190610df6565b50565b5080546000825590600052602060002090810190610dd59190610e0b565b5b80821115610d235760008155600101610df7565b80821115610d23576000610e1f8282610d9b565b50600101610e0b565b634e487b7160e01b600052604160045260246000fd5b600082601f830112610e4f57600080fd5b813567ffffffffffffffff80821115610e6a57610e6a610e28565b604051601f8301601f19908116603f01168101908282118183101715610e9257610e92610e28565b81604052838152866020858801011115610eab57600080fd5b836020870160208301376000602085830101528094505050505092915050565b60008060408385031215610ede57600080fd5b823567ffffffffffffffff80821115610ef657600080fd5b610f0286838701610e3e565b93506020850135915080821115610f1857600080fd5b50610f2585828601610e3e565b9150509250929050565b600060208284031215610f4157600080fd5b813567ffffffffffffffff811115610f5857600080fd5b610f6484828501610e3e565b949350505050565b60005b83811015610f87578181015183820152602001610f6f565b838111156106e65750506000910152565b6020815260008251806020840152610fb7816040850160208701610f6c565b601f01601f19169190910160400192915050565b600080600080600060a08688031215610fe357600080fd5b853567ffffffffffffffff80821115610ffb57600080fd5b61100789838a01610e3e565b9650602088013591508082111561101d57600080fd5b61102989838a01610e3e565b9550604088013591506001600160a01b038216821461104757600080fd5b9093506060870135908082111561105d57600080fd5b61106989838a01610e3e565b9350608088013591508082111561107f57600080fd5b5061108c88828901610e3e565b9150509295509295909350565b600082516110ab818460208701610f6c565b9190910192915050565b634e487b7160e01b600052601160045260246000fd5b6000828210156110dd576110dd6110b5565b500390565b634e487b7160e01b600052603260045260246000fd5b600181811c9082168061110c57607f821691505b6020821081141561112d57634e487b7160e01b600052602260045260246000fd5b50919050565b600080835481600182811c91508083168061114f57607f831692505b602080841082141561116f57634e487b7160e01b86526022600452602486fd5b8180156111835760018114611194576111c1565b60ff198616895284890196506111c1565b60008a81526020902060005b868110156111b95781548b8201529085019083016111a0565b505084890196505b509498975050505050505050565b60006000198214156111e3576111e36110b5565b5060010190565b634e487b7160e01b600052603160045260246000fdfea2646970667358221220abe0879277cf29e4190f55106b8bd114a6fae3891bbc9004b8729a09521db4f164736f6c634300080b0033";

    public static final String FUNC_CONTAINS = "contains";

    public static final String FUNC_DELETEFRIEND = "deleteFriend";

    public static final String FUNC_GETETHADDRESS = "getEthAddress";

    public static final String FUNC_GETIPFSHASH = "getIPFSHash";

    public static final String FUNC_GETPASSWORD = "getPassword";

    public static final String FUNC_GETLIBP2PADDRESS = "getlibp2pAddress";

    public static final String FUNC_REMOVE = "remove";

    public static final String FUNC_SET = "set";

    public static final String FUNC_SETFRIEND = "setFriend";

    public static final String FUNC_SETIPFSHASH = "setIPFSHash";

    public static final String FUNC_SETLIBP2PADDR = "setLibp2pAddr";

    public static final String FUNC_SETPASSWORD = "setPassword";

    public static final String FUNC_SIZE = "size";

    @Deprecated
    protected FriendsManagement(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FriendsManagement(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FriendsManagement(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FriendsManagement(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<Boolean> contains(String _key) {
        final Function function = new Function(FUNC_CONTAINS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> deleteFriend(String _key, String _frName) {
        final Function function = new Function(
                FUNC_DELETEFRIEND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key), 
                new org.web3j.abi.datatypes.Utf8String(_frName)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> getEthAddress(String _key) {
        final Function function = new Function(FUNC_GETETHADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getIPFSHash(String _key) {
        final Function function = new Function(FUNC_GETIPFSHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getPassword(String _key) {
        final Function function = new Function(FUNC_GETPASSWORD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getlibp2pAddress(String _key) {
        final Function function = new Function(FUNC_GETLIBP2PADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> remove(String _key) {
        final Function function = new Function(
                FUNC_REMOVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> set(String _key, String _libp2pAddr, String _userAddr, String _pass, String _ipfs) {
        final Function function = new Function(
                FUNC_SET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key), 
                new org.web3j.abi.datatypes.Utf8String(_libp2pAddr), 
                new org.web3j.abi.datatypes.Address(160, _userAddr), 
                new org.web3j.abi.datatypes.Utf8String(_pass), 
                new org.web3j.abi.datatypes.Utf8String(_ipfs)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setFriend(String _key, String _friendName) {
        final Function function = new Function(
                FUNC_SETFRIEND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key), 
                new org.web3j.abi.datatypes.Utf8String(_friendName)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setIPFSHash(String _key, String _ipfs) {
        final Function function = new Function(
                FUNC_SETIPFSHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key), 
                new org.web3j.abi.datatypes.Utf8String(_ipfs)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setLibp2pAddr(String _key, String _libp2pAddr) {
        final Function function = new Function(
                FUNC_SETLIBP2PADDR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key), 
                new org.web3j.abi.datatypes.Utf8String(_libp2pAddr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setPassword(String _key, String _pass) {
        final Function function = new Function(
                FUNC_SETPASSWORD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_key), 
                new org.web3j.abi.datatypes.Utf8String(_pass)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> size() {
        final Function function = new Function(FUNC_SIZE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static FriendsManagement load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FriendsManagement(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FriendsManagement load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FriendsManagement(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FriendsManagement load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FriendsManagement(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FriendsManagement load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FriendsManagement(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<FriendsManagement> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(FriendsManagement.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<FriendsManagement> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FriendsManagement.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<FriendsManagement> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(FriendsManagement.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<FriendsManagement> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FriendsManagement.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
