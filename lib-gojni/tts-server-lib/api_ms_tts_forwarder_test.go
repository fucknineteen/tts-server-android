package tts_server_lib

import (
	"fmt"
	"testing"
)

func TestMsTtsForwarder(t *testing.T) {
	s := MsTtsForwarder{UseDNS: true}
	s.Init(new(logCallback))
	s.Start(1233)
}

type logCallback struct {
}

func (l *logCallback) OnLog(level int32, msg string) {
	fmt.Println(msg)
}
