package main

/*
#cgo CFLAGS: -fPIC
*/
import "C"
import "github.com/danbrough/golibtest/misc"

//export GetTime
func GetTime() *C.char {
	return C.CString(misc.GetTime())
}

func main() {

}