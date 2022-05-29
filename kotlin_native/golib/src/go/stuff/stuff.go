package stuff

import "C"

import (
	"fmt"
	"time"
)

//export GetMessage
func GetMessage() string {
	return fmt.Sprintf("The local time is %s", time.Now().Local().String())
}
