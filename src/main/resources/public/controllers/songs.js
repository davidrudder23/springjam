function Songs($scope, $http, $rootScope) {
    $rootScope.$watch('selectedBand', function() {
        $http.defaults.headers.common.Authorization = 'Basic ' + btoa(localStorage.getItem("email") + ":" + localStorage.getItem("password"));
        $http.get('/api/band/' + $rootScope.selectedBand.id + '/songs').
            error(function (data) {
                window.location = "/login.html";
            }).
            success(function (data) {
                $scope.songs = data;
            }).then(function (data) {
                $http.defaults.headers.common.Authorization = 'Basic ' + btoa(localStorage.getItem("email") + ":" + localStorage.getItem("password"));

                $http.get('/api/song/0/seen').
                    success(function (data) {
                        $scope.seenSongs = data;
                    }).then(function (data) {
                        angular.forEach($scope.songs, function (song, idx) {
                            song.seen = false;

                            angular.forEach($scope.seenSongs, function (seenSong, songIdx) {
                                if (song.id == seenSong.id) {
                                    song.seen = true;
                                }
                            })
                        });
                    })
            });

        $scope.sortBy = 'seen';
        $scope.sortReverse = true;
    });

    $scope.showSong = function(id) {
        $http.defaults.headers.common.Authorization = 'Basic '+btoa(localStorage.getItem("email")+":"+localStorage.getItem("password"));
        $http.get("/api/song/"+id).
            success(function(data) {
                $scope.song = data;
            }).then(function() {
                console.log($scope.song);
                angular.forEach($scope.song.performances, function (performance, idx) {
                    console.log(performance);
                    console.log("Looking for concert "+performance.concert);
                    $http.get("/api/concert/" + performance.concert).
                        success(function (data) {
                            performance.concert = data;
                        });
                });
            });
    }
}