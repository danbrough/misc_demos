package stuff

import "C"

import (
	"fmt"
	"time"
)

//export GetMessage
func GetMessage() string {
	return fmt.Sprintf("Local Time: %s", time.Now().Local().String())
}
