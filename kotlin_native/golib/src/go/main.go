package main

/*
#cgo CFLAGS: -fPIC
*/
import "C"

import (
	"fmt"
	"time"
)

//export GetMessage
func GetMessage() string {
	return fmt.Sprintf("LocalTime %s", time.Now().Local().String())
}

func main() {
	println("The message is:", GetMessage())
}
