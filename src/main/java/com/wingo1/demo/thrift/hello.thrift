namespace java com.wingo1.demo.thrift.stub

struct Data {
1: i32 uid,
2: string desc,
3: binary content
}

service HelloService{
	Data getData(1:i32 id)
}