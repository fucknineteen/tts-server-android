package tts_server_lib

import (
	"github.com/jing332/tts-server-go/server"
	log "github.com/sirupsen/logrus"
	"tts-server-lib/wrapper"
)

type MsTtsForwarder struct {
	UseDNS bool
	Token  string

	server *server.GracefulServer
}

func (m *MsTtsForwarder) Start(port int64) bool {
	m.server.HandleFunc()
	err := m.server.ListenAndServe(port)
	if err != nil {
		log.Errorln(err)
		return false
	}

	return true
}

func (m *MsTtsForwarder) Shutdown() {
	m.server.Close()
}

type LogCallback interface {
	OnLog(level int32, msg string)
}

func (m *MsTtsForwarder) Init(callback LogCallback) {
	log.SetFormatter(&wrapper.MyFormatter{
		OnLog: callback.OnLog})

	m.server = &server.GracefulServer{
		UseDnsEdge: m.UseDNS,
		Token:      m.Token,
	}
}
