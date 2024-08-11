package tts_server_lib

import (
	"os"
	"testing"
)

func TestEdge(t *testing.T) {
	e := &EdgeApi{}
	pro := &VoiceProperty{VoiceName: "zh-CN-XiaoxiaoNeural"}
	prosody := &VoiceProsody{Rate: 10, Volume: 50, Pitch: 0}
	data, err := e.GetEdgeAudio("我是测试文本",
		"webm-24khz-16bit-mono-opus", pro, prosody)
	if err != nil {
		t.Error(err)
	}

	t.Log(len(data))
	file, err := os.OpenFile("./edge.mp3", os.O_RDWR|os.O_TRUNC|os.O_CREATE, 0766)
	if err != nil {
		t.Fatal(err)
		return
	}
	file.Write(data)
	file.Close()
}
