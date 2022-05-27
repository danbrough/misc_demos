package stuff

import "C"

import (
	"fmt"
	"time"
)

//export GetMessage
func GetMessage() string {
	return fmt.Sprintf("Local time: %s", time.Now().Local().String())
}
